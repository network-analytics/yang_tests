import org.junit.jupiter.api.Test;

public class AnydataValidationTest {

    @Test
    void testPrimitiveTypeAnydata() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/anydata",
                "../data/anydata/primitive-anydata.json"
        );
    }

    @Test
    void testObjectWithSchemaAnydata() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/anydata",
                "../data/anydata/object-with-schema-anydata.json"
        );
    }

    @Test
    void testObjectWithoutSchemaAnydata() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/anydata",
                "../data/anydata/object-without-schema-anydata.json"
        );
    }

    @Test
    void testPrimitiveAnydataInAnydata() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/anydata-anydata",
                "../data/anydata-in-anydata/primitive-anydata.json"
        );
    }

    @Test
    void testObjectWithSchemaAnydataInAnydata() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/anydata-anydata",
                "../data/anydata-in-anydata/object-with-schema-anydata-anydata.json"
        );
    }

    @Test
    void testObjectWithoutSchemaAnydataInAnydata() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/anydata-anydata",
                "../data/anydata-in-anydata/object-without-schema-anydata-anydata.json"
        );
    }

    @Test
    void testNullAnydata() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/anydata",
                "../data/anydata/null-anydata.json"
        );
    }

    @Test
    void testEmptyObjectAnydata() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/anydata",
                "../data/anydata/empty-object-anydata.json"
        );
    }

    @Test
    void testEmptyArrayAnydata() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/anydata",
                "../data/anydata/empty-array-anydata.json"
        );
    }

    @Test
    void testPrimitiveArrayAnydata() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/anydata",
                "../data/anydata/primitive-array-anydata.json"
        );
    }

    @Test
    void testObjectArrayAnydata() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/anydata",
                "../data/anydata/object-array-anydata.json"
        );
    }

    @Test
    void testAnydataCrossModuleIdentityref() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/anydata-identity",
                "../data/anydata-identity/cross-module-identityref-anydata.json"
        );
    }

    @Test
    void testAnydataCrossModuleIdentityrefMissingModule() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/anydata-identity-missing",
                "../data/anydata-identity/cross-module-identityref-missing-module-anydata.json"
        );
    }
}