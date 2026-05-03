import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.yangcentral.yangkit.parser.YangParserException;

import java.io.IOException;

public class SchemaValidationTest {

    @Test
    void testLoadValidSchema() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadValidSchema("../yang/schema-validation/valid-schema-test.yang");
    }

    @Test
    void testLoadInvalidSchema() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadInvalidSchema("../yang/schema-validation/invalid-schema-test.yang");
    }

    @Test
    void testLoadInvalidSchemaSemantics() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadInvalidSchema("../yang/schema-validation/semantic-error-example.yang");
    }
}
