import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNodeContainer;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.model.api.stmt.FeatureSet;
import org.opendaylight.yangtools.yang.model.spi.source.FileYangTextSource;
import org.opendaylight.yangtools.yang.parser.api.YangParserFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class YangLibraryUtils {

    private YangLibraryUtils() {
    }

    private static QName featureQName(YangFileInfo info, String feature) {
        if (info.revision == null || info.revision.isBlank()) {
            return QName.create(info.namespace, feature).intern();
        }

        return QName.create(info.namespace, info.revision, feature).intern();
    }

    public static EffectiveModelContext newContextFromYangLibraryData(
            Path searchDir,
            NormalizedNode ylibTree
    ) throws Exception {
        if (searchDir == null) {
            throw new IllegalArgumentException("searchDir is null");
        }

        if (ylibTree == null) {
            throw new IllegalArgumentException("ylibTree is null");
        }

        Path dir = searchDir.toAbsolutePath().normalize();

        if (!Files.exists(dir)) {
            throw new IllegalArgumentException("Search directory not found: " + dir);
        }

        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Search path is not a directory: " + dir);
        }

        List<ModuleSpec> implementedModules = extractModuleSpecs(ylibTree);

        if (implementedModules.isEmpty()) {
            throw new IllegalArgumentException("No module entries found in yang-library data");
        }

        List<YangFileInfo> yangFiles = resolveYangFiles(dir);

        if (yangFiles.isEmpty()) {
            throw new IllegalArgumentException("No .yang files found in directory: " + dir);
        }

        Set<ModuleKey> implementedKeys = new HashSet<>();
        Set<QName> supportedFeatures = new HashSet<>();

        for (ModuleSpec spec : implementedModules) {
            Optional<YangFileInfo> moduleFile = findMatchingModule(yangFiles, spec);

            if (moduleFile.isEmpty()) {
                throw new IllegalArgumentException(
                        "Module not found in search directory: name="
                                + spec.name
                                + ", revision="
                                + spec.revision
                );
            }

            YangFileInfo info = moduleFile.get();
            implementedKeys.add(new ModuleKey(info.name, info.revision));

            for (String feature : spec.features) {
                if (info.namespace == null || info.namespace.isBlank()) {
                    throw new IllegalArgumentException(
                            "Cannot resolve feature namespace for module: " + info.name
                    );
                }

                supportedFeatures.add(
                        featureQName(info, feature)
                );
            }
        }

        var factory = ServiceLoader.load(YangParserFactory.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No YangParserFactory found — check yang-parser-rfc7950 is on the classpath"
                ));

        var parser = factory.createParser();

        for (YangFileInfo info : yangFiles) {
            var source = new FileYangTextSource(info.path);

            if (!info.submodule && implementedKeys.contains(new ModuleKey(info.name, info.revision))) {
                parser.addSource(source);
            } else {
                parser.addLibSource(source);
            }
        }

        parser.setSupportedFeatures(FeatureSet.of(supportedFeatures));

        return parser.buildEffectiveModel();
    }

    public static EffectiveModelContext newContextFromYangLibraryData(
            String searchDir,
            NormalizedNode ylibTree
    ) throws Exception {
        return newContextFromYangLibraryData(Path.of(searchDir), ylibTree);
    }

    private static List<ModuleSpec> extractModuleSpecs(NormalizedNode ylibTree) {
        List<NormalizedNode> moduleNodes = new ArrayList<>();
        collectNodesByLocalName(ylibTree, "module", moduleNodes);

        List<ModuleSpec> specs = new ArrayList<>();

        for (NormalizedNode moduleNode : moduleNodes) {
            String name = getSingleStringChild(moduleNode, "name");
            String revision = getSingleStringChild(moduleNode, "revision");
            List<String> features = getStringDescendants(moduleNode, "feature");

            if (name == null || name.isBlank()) {
                continue;
            }

            if (revision != null && revision.isBlank()) {
                revision = null;
            }

            specs.add(new ModuleSpec(name, revision, features));
        }

        return specs;
    }

    private static void collectNodesByLocalName(
            NormalizedNode node,
            String localName,
            List<NormalizedNode> result
    ) {
        if (localName.equals(localNameOf(node))) {
            result.add(node);
        }

        for (NormalizedNode child : childrenOf(node)) {
            collectNodesByLocalName(child, localName, result);
        }
    }

    private static String getSingleStringChild(
            NormalizedNode container,
            String childName
    ) {
        for (NormalizedNode child : childrenOf(container)) {
            if (!childName.equals(localNameOf(child))) {
                continue;
            }

            String value = scalarStringValue(child);

            if (value != null) {
                return value;
            }
        }

        return null;
    }

    private static List<String> getStringDescendants(
            NormalizedNode node,
            String childName
    ) {
        List<NormalizedNode> matchingNodes = new ArrayList<>();
        collectNodesByLocalName(node, childName, matchingNodes);

        List<String> values = new ArrayList<>();

        for (NormalizedNode matchingNode : matchingNodes) {
            String value = scalarStringValue(matchingNode);

            if (value != null) {
                values.add(value);
            }
        }

        return values;
    }

    private static String scalarStringValue(NormalizedNode node) {
        if (node instanceof NormalizedNodeContainer<?>) {
            return null;
        }

        Object body = node.body();

        if (body == null) {
            return null;
        }

        return body.toString();
    }

    private static String localNameOf(NormalizedNode node) {
        return node.name().getNodeType().getLocalName();
    }

    private static List<NormalizedNode> childrenOf(NormalizedNode node) {
        List<NormalizedNode> children = new ArrayList<>();

        if (!(node instanceof NormalizedNodeContainer<?> container)) {
            return children;
        }

        for (Object childObj : container.body()) {
            children.add((NormalizedNode) childObj);
        }

        return children;
    }

    private static List<YangFileInfo> resolveYangFiles(Path dir) throws IOException {
        List<Path> paths;

        try (var stream = Files.walk(dir)) {
            paths = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".yang"))
                    .sorted()
                    .toList();
        }

        List<YangFileInfo> files = new ArrayList<>();

        for (Path path : paths) {
            files.add(readYangFileInfo(path));
        }

        return files;
    }

    private static Optional<YangFileInfo> findMatchingModule(
            List<YangFileInfo> files,
            ModuleSpec spec
    ) {
        return files.stream()
                .filter(info -> !info.submodule)
                .filter(info -> spec.name.equals(info.name))
                .filter(info -> spec.revision == null || spec.revision.equals(info.revision))
                .findFirst();
    }

    private static YangFileInfo readYangFileInfo(Path path) throws IOException {
        String text = Files.readString(path);

        Pattern modulePattern = Pattern.compile(
                "(?m)^\\s*(module|submodule)\\s+([A-Za-z_][A-Za-z0-9_.-]*)\\s*\\{"
        );

        Pattern namespacePattern = Pattern.compile(
                "\\bnamespace\\s+[\"']([^\"']+)[\"']\\s*;"
        );

        Pattern revisionPattern = Pattern.compile(
                "\\brevision\\s+[\"']?([0-9]{4}-[0-9]{2}-[0-9]{2})[\"']?\\s*(?:;|\\{)"
        );

        Matcher moduleMatcher = modulePattern.matcher(text);

        if (!moduleMatcher.find()) {
            throw new IllegalArgumentException("Unable to find module/submodule name in: " + path);
        }

        boolean submodule = "submodule".equals(moduleMatcher.group(1));
        String name = moduleMatcher.group(2);

        String namespace = null;
        Matcher namespaceMatcher = namespacePattern.matcher(text);

        if (namespaceMatcher.find()) {
            namespace = namespaceMatcher.group(1);
        }

        String revision = null;
        Matcher revisionMatcher = revisionPattern.matcher(text);

        if (revisionMatcher.find()) {
            revision = revisionMatcher.group(1);
        }

        return new YangFileInfo(path, name, revision, namespace, submodule);
    }

    private static final class YangFileInfo {
        private final Path path;
        private final String name;
        private final String revision;
        private final String namespace;
        private final boolean submodule;

        private YangFileInfo(
                Path path,
                String name,
                String revision,
                String namespace,
                boolean submodule
        ) {
            this.path = path;
            this.name = name;
            this.revision = revision;
            this.namespace = namespace;
            this.submodule = submodule;
        }
    }

    private static final class ModuleSpec {
        private final String name;
        private final String revision;
        private final List<String> features;

        private ModuleSpec(String name, String revision, List<String> features) {
            this.name = name;
            this.revision = revision;
            this.features = features;
        }
    }

    private static final class ModuleKey {
        private final String name;
        private final String revision;

        private ModuleKey(String name, String revision) {
            this.name = name;
            this.revision = revision;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ModuleKey other)) {
                return false;
            }

            return java.util.Objects.equals(name, other.name)
                    && java.util.Objects.equals(revision, other.revision);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(name, revision);
        }
    }
}