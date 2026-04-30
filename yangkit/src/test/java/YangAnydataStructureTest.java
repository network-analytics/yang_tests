import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.yangcentral.yangkit.parser.YangParserException;

import java.io.IOException;

public class YangAnydataStructureTest {

    // primitive type should not be allowed in anydata
    @Test
    void testPrimitiveTypeAnydataInStructure() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadInvalidYangDataDocParseError("../yang/anydata-in-structure",
                "../data/anydata-in-structure/primitive-anydata-in-structure.json");
    }

    @Test
    void testObjectWithSchemaAnydataInStructure() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadValidYangDataDoc("../yang/anydata-in-structure",
                "../data/anydata-in-structure/object-with-schema-anydata-in-structure.json");
    }

    @Test
    void testObjectWithoutSchemaAnydataStructure() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadInvalidYangDataDocParseError("../yang/anydata-in-structure",
                "../data/anydata-in-structure/object-without-schema-anydata-in-structure.json");
    }

    @Test
    void testNullAnydataInStructure() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadValidYangDataDoc("../yang/anydata-in-structure",
                "../data/anydata-in-structure/null-anydata-in-structure.json");
    }

    @Test
    void testArrayAnydataInStructure() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadInvalidYangDataDocParseError("../yang/anydata-in-structure",
                "../data/anydata-in-structure/array-anydata-in-structure.json");
    }

    @Test
    void testNullObjectAnydataInStructure() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadValidYangDataDoc("../yang/anydata-in-structure",
                "../data/anydata-in-structure/null-object-anydata-in-structure.json");
    }

    @Test
    void testStructureInAnydata() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadValidYangDataDoc("../yang/structure-in-anydata",
                "../data/structure-in-anydata/structure-in-anydata.json");
    }
}
