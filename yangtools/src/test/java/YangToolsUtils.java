import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.model.spi.source.FileYangTextSource;
import org.opendaylight.yangtools.yang.parser.api.YangParserFactory;

import java.nio.file.Paths;
import java.util.List;
import java.util.ServiceLoader;

public final class YangToolsUtils {
    public static EffectiveModelContext loadSchema(List<String> files) throws Exception {
        YangParserFactory factory = ServiceLoader.load(YangParserFactory.class)
                .findFirst()
                .orElseThrow();

        var parser = factory.createParser();

        for (String file : files) {
            var f = Paths.get(file).toAbsolutePath();
            parser.addSource(new FileYangTextSource(f));
        }

        return parser.buildEffectiveModel();
    }

}

