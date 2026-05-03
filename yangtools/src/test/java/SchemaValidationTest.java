import org.junit.jupiter.api.Test;

public class SchemaValidationTest {

    @Test
    void testLoadValidSchema() throws Exception {
        YangToolsUtils.loadValidSchema("../yang/schema-validation/valid-schema-test.yang");
    }

    @Test
    void testLoadInvalidSchema() throws Exception {
        YangToolsUtils.loadInvalidSchema("../yang/schema-validation/invalid-schema-test.yang");
    }

    @Test
    void testLoadInvalidSchemaSemantics() throws Exception {
        YangToolsUtils.loadInvalidSchema("../yang/schema-validation/semantic-error-example.yang");
    }
}