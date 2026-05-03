import org.junit.jupiter.api.Test;

public class YangAnydataStructureTest {

    @Test
    void testPrimitiveTypeAnydataInStructure() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/anydata-in-structure",
                "../data/anydata-in-structure/primitive-anydata-in-structure.json"
        );
    }

    @Test
    void testObjectWithSchemaAnydataInStructure() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/anydata-in-structure",
                "../data/anydata-in-structure/object-with-schema-anydata-in-structure.json"
        );
    }

    @Test
    void testObjectWithoutSchemaAnydataStructure() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/anydata-in-structure",
                "../data/anydata-in-structure/object-without-schema-anydata-in-structure.json"
        );
    }

    @Test
    void testNullAnydataInStructure() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/anydata-in-structure",
                "../data/anydata-in-structure/null-anydata-in-structure.json"
        );
    }

    @Test
    void testArrayAnydataInStructure() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/anydata-in-structure",
                "../data/anydata-in-structure/array-anydata-in-structure.json"
        );
    }

    @Test
    void testNullObjectAnydataInStructure() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/anydata-in-structure",
                "../data/anydata-in-structure/null-object-anydata-in-structure.json"
        );
    }

    @Test
    void testStructureInAnydata() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/structure-in-anydata",
                "../data/structure-in-anydata/structure-in-anydata.json"
        );
    }
}