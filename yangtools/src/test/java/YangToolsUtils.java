import com.google.gson.stream.JsonReader;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;
import org.opendaylight.yangtools.yang.data.codec.gson.JsonParserStream;
import org.opendaylight.yangtools.yang.data.impl.schema.ImmutableNormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.impl.schema.NormalizationResultHolder;
import org.opendaylight.yangtools.yang.data.spi.node.MandatoryLeafEnforcer;
import org.opendaylight.yangtools.yang.data.tree.api.DataTree;
import org.opendaylight.yangtools.yang.data.tree.api.DataTreeConfiguration;
import org.opendaylight.yangtools.yang.data.tree.api.DataTreeFactory;
import org.opendaylight.yangtools.yang.data.tree.dagger.ReferenceDataTreeFactoryModule;
import org.opendaylight.yangtools.yang.data.util.DataSchemaContextTree;
import org.opendaylight.yangtools.yang.model.api.DataNodeContainer;
import org.opendaylight.yangtools.yang.model.api.DataSchemaNode;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.model.spi.source.FileYangTextSource;
import org.opendaylight.yangtools.yang.parser.api.YangParserFactory;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class YangToolsUtils {

    public static boolean DEBUG = true;

    private YangToolsUtils() {
    }

    private static void debugException(String title, Exception e) {
        if (!DEBUG) return;

        System.out.println("==== " + title + " ====");
        System.out.println(e.getClass().getName() + ": " + e.getMessage());

        if (e.getCause() != null) {
            System.out.println("Cause: " + e.getCause().getClass().getName() + ": " + e.getCause().getMessage());
        }
    }

    public static EffectiveModelContext loadValidSchema(String yangPath) throws Exception {
        return loadSchema(yangPath, false);
    }

    public static EffectiveModelContext loadInvalidSchema(String yangPath) throws Exception {
        return loadSchema(yangPath, true);
    }

    public static EffectiveModelContext loadValidSchema(List<String> yangPaths) throws Exception {
        return loadSchema(yangPaths, false);
    }

    public static EffectiveModelContext loadInvalidSchema(List<String> yangPaths) throws Exception {
        return loadSchema(yangPaths, true);
    }

    public static EffectiveModelContext loadSchema(String yangPath) throws Exception {
        return loadSchema(yangPath, false);
    }

    public static EffectiveModelContext loadSchema(List<String> yangPaths) throws Exception {
        return loadSchema(yangPaths, false);
    }

    public static EffectiveModelContext loadSchema(String yangPath, boolean expectError) throws Exception {
        return loadSchema(List.of(yangPath), expectError);
    }

    public static EffectiveModelContext loadSchema(List<String> yangPaths, boolean expectError) throws Exception {
        EffectiveModelContext context = null;
        boolean getError = false;
        Exception caughtException = null;

        try {
            List<Path> yangFiles = resolveYangPaths(yangPaths);
            context = parseYangFiles(yangFiles);
        } catch (Exception e) {
            getError = true;
            caughtException = e;
        }

        if (caughtException != null) {
            debugException("Schema error", caughtException);
        }

        if (!expectError) {
            assertNotNull(context, "context is null");
        }

        assertEquals(
                expectError,
                getError,
                expectError
                        ? "Expected invalid schema but schema is valid"
                        : "Expected valid schema but schema is not valid"
        );

        return context;
    }

    private static List<Path> resolveYangPaths(List<String> yangPaths) throws Exception {
        if (yangPaths == null || yangPaths.isEmpty()) {
            throw new IllegalArgumentException("At least one YANG path must be provided");
        }

        Set<Path> resolvedFiles = new LinkedHashSet<>();

        for (String yangPath : yangPaths) {
            if (yangPath == null || yangPath.isBlank()) {
                throw new IllegalArgumentException("YANG path must not be blank");
            }

            Path path = Path.of(yangPath).toAbsolutePath().normalize();

            if (!Files.exists(path)) {
                throw new IllegalArgumentException("YANG path not found: " + path);
            }

            if (Files.isRegularFile(path)) {
                if (!path.toString().endsWith(".yang")) {
                    throw new IllegalArgumentException("YANG file must end with .yang: " + path);
                }

                resolvedFiles.add(path);
                continue;
            }

            if (Files.isDirectory(path)) {
                try (var stream = Files.walk(path)) {
                    stream
                            .filter(Files::isRegularFile)
                            .filter(file -> file.toString().endsWith(".yang"))
                            .sorted()
                            .forEach(resolvedFiles::add);
                }

                continue;
            }

            throw new IllegalArgumentException("YANG path is neither file nor folder: " + path);
        }

        if (resolvedFiles.isEmpty()) {
            throw new IllegalArgumentException("No .yang files found in provided paths: " + yangPaths);
        }

        return new ArrayList<>(resolvedFiles);
    }

    private static EffectiveModelContext parseYangFiles(List<Path> yangFiles) throws Exception {
        var factory = ServiceLoader.load(YangParserFactory.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No YangParserFactory found — check yang-parser-rfc7950 is on the classpath"
                ));

        var parser = factory.createParser();

        for (Path path : yangFiles) {
            parser.addSource(new FileYangTextSource(path));
        }

        return parser.buildEffectiveModel();
    }

    public static NormalizedNode loadValidNormalizedNode(
            EffectiveModelContext context,
            String jsonFile
    ) {
        return loadNormalizedNode(context, jsonFile, false, false);
    }

    public static NormalizedNode loadValidNormalizedNode(
            String yangPath,
            String jsonFile
    ) throws Exception {
        EffectiveModelContext context = loadValidSchema(yangPath);
        return loadNormalizedNode(context, jsonFile, false, false);
    }

    public static NormalizedNode loadValidNormalizedNode(
            List<String> yangPaths,
            String jsonFile
    ) throws Exception {
        EffectiveModelContext context = loadValidSchema(yangPaths);
        return loadNormalizedNode(context, jsonFile, false, false);
    }

    public static NormalizedNode loadInvalidNormalizedNodeParseError(
            EffectiveModelContext context,
            String jsonFile
    ) {
        return loadNormalizedNode(context, jsonFile, true, false);
    }

    public static NormalizedNode loadInvalidNormalizedNodeParseError(
            String yangPath,
            String jsonFile
    ) throws Exception {
        EffectiveModelContext context = loadValidSchema(yangPath);
        return loadNormalizedNode(context, jsonFile, true, false);
    }

    public static NormalizedNode loadInvalidNormalizedNodeParseError(
            List<String> yangPaths,
            String jsonFile
    ) throws Exception {
        EffectiveModelContext context = loadValidSchema(yangPaths);
        return loadNormalizedNode(context, jsonFile, true, false);
    }

    public static NormalizedNode loadInvalidNormalizedNodeValidateError(
            EffectiveModelContext context,
            String jsonFile
    ) {
        return loadNormalizedNode(context, jsonFile, false, true);
    }

    public static NormalizedNode loadInvalidNormalizedNodeValidateError(
            String yangPath,
            String jsonFile
    ) throws Exception {
        EffectiveModelContext context = loadValidSchema(yangPath);
        return loadNormalizedNode(context, jsonFile, false, true);
    }

    public static NormalizedNode loadInvalidNormalizedNodeValidateError(
            List<String> yangPaths,
            String jsonFile
    ) throws Exception {
        EffectiveModelContext context = loadValidSchema(yangPaths);
        return loadNormalizedNode(context, jsonFile, false, true);
    }

    public static NormalizedNode loadNormalizedNode(
            EffectiveModelContext context,
            String jsonFile,
            boolean expectErrorDuringParsing,
            boolean expectErrorDuringValidation
    ) {
        NormalizedNode data = null;

        boolean parseError = false;
        Exception parseException = null;

        try {
            data = parseJson(context, jsonFile);
            parseError = data == null;
        } catch (Exception e) {
            parseError = true;
            parseException = e;
        }

        if (parseException != null) {
            debugException("JSON parsing error", parseException);
        }

        assertEquals(
                expectErrorDuringParsing,
                parseError,
                expectErrorDuringParsing
                        ? "Expected error during parsing but no error"
                        : "Expected no error during parsing but data is not valid"
        );

        if (expectErrorDuringParsing) {
            return data;
        }

        assertNotNull(data, "data is null");

        boolean validationError = false;
        Exception validationException = null;

        try {
            validateData(context, data);
        } catch (Exception e) {
            validationError = true;
            validationException = e;
        }

        if (validationException != null) {
            debugException("Data validation error", validationException);
        }

        assertEquals(
                expectErrorDuringValidation,
                validationError,
                expectErrorDuringValidation
                        ? "Expected error during validation but no error"
                        : "Expected no error during validation but data is not valid"
        );

        return data;
    }

    public static NormalizedNode parseJson(
            EffectiveModelContext context,
            String jsonFile
    ) throws Exception {
        assertNotNull(context, "context is null");

        Path jsonPath = Path.of(jsonFile).toAbsolutePath().normalize();

        if (!Files.exists(jsonPath)) {
            throw new IllegalArgumentException("JSON file not found: " + jsonPath);
        }

        NormalizationResultHolder resultHolder = new NormalizationResultHolder();
        var writer = ImmutableNormalizedNodeStreamWriter.from(resultHolder);

        var parser = JsonParserStream.create(
                writer,
                JSONCodecFactorySupplier.RFC7951.getShared(context)
        );

        try (
                var inputStream = Files.newInputStream(jsonPath);
                var reader = new JsonReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8)
                )
        ) {
            parser.parse(reader);
        }

        var result = resultHolder.getResult();
        assertNotNull(result, "normalization result is null");

        return result.data();
    }

    public static void validateData(
            EffectiveModelContext context,
            NormalizedNode data
    ) throws Exception {
        assertNotNull(context, "context is null");
        assertNotNull(data, "data is null");

        validateWithDataTree(context, data);
        validateMandatoryLeavesIfPossible(context, data);
    }

    private static void validateWithDataTree(
            EffectiveModelContext context,
            NormalizedNode data
    ) throws Exception {
        DataTree dataTree = newOperationalTree(context);

        YangInstanceIdentifier path = YangInstanceIdentifier.of(data.name());

        var modification = dataTree.takeSnapshot().newModification();

        modification.write(path, data);
        modification.ready();

        dataTree.validate(modification);
    }

    private static void validateMandatoryLeavesIfPossible(
            EffectiveModelContext context,
            NormalizedNode data
    ) {
        YangInstanceIdentifier path = YangInstanceIdentifier.of(data.name());

        Optional<DataSchemaNode> maybeSchemaNode = findDataSchemaNode(context, path);

        if (maybeSchemaNode.isEmpty()) {
            return;
        }

        DataSchemaNode schemaNode = maybeSchemaNode.get();

        if (!(schemaNode instanceof DataNodeContainer containerSchema)) {
            return;
        }

        MandatoryLeafEnforcer enforcer = MandatoryLeafEnforcer.forContainer(
                containerSchema,
                false
        );

        if (enforcer == null) {
            return;
        }

        enforcer.enforceOnData(data);
    }

    private static Optional<DataSchemaNode> findDataSchemaNode(
            EffectiveModelContext context,
            YangInstanceIdentifier path
    ) {
        try {
            return DataSchemaContextTree.from(context)
                    .findChild(path)
                    .map(schemaContext -> schemaContext.dataSchemaNode());
        } catch (RuntimeException e) {
            if (DEBUG) {
                System.out.println("Unable to resolve schema node for path: " + path);
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }

            return Optional.empty();
        }
    }


    public static void printDataTree(NormalizedNode node) {
        if (node == null) {
            System.out.println("<null>");
            return;
        }

        System.out.println(node.prettyTree().get());
    }

    public static DataTree newOperationalTree(EffectiveModelContext schemaContext) {
        DataTreeFactory factory = ReferenceDataTreeFactoryModule.provideDataTreeFactory();
        return factory.create(DataTreeConfiguration.DEFAULT_OPERATIONAL, schemaContext);
    }

    public static YangInstanceIdentifier.NodeIdentifier getIdentifier(
            String namespace,
            String localName
    ) {
        QName qname = QName.create(namespace, localName).intern();
        return new YangInstanceIdentifier.NodeIdentifier(qname);
    }
}