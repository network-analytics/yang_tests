import org.junit.jupiter.api.Test;
import org.opendaylight.yangtools.yang.model.spi.source.FileYangTextSource;
import org.opendaylight.yangtools.yang.parser.api.YangParserException;
import org.opendaylight.yangtools.yang.parser.api.YangParserFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.*;

public class ModuleInspectionTest {
    @Test
    void testModuleInspection() throws YangParserException, IOException {
        YangParserFactory factory = ServiceLoader.load(YangParserFactory.class)
                .findFirst()
                .orElseThrow();

        var parser = factory.createParser();

        var f = Paths.get("../yang/schema-test.yang").toAbsolutePath();
        parser.addSource(new FileYangTextSource(f));
        String namespace = parser.buildDeclaredModel().getFirst().declaredSubstatements().get(1).argument().toString();
        assertEquals("urn:schema:test", namespace);


    }
}
