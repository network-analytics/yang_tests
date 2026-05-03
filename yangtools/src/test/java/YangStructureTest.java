import org.junit.jupiter.api.Test;

public class YangStructureTest {

    @Test
    void testValidMinimalJsonStructure() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/scotthuang-structure",
                "../data/structure/scotthuang_valid_structure_minimal.json"
        );
    }

    @Test
    void testValidJsonStructure() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/scotthuang-structure",
                "../data/structure/scotthuang_valid_structure_message.json"
        );
    }

    @Test
    void testInvalidTypeJsonStructure() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/scotthuang-structure",
                "../data/structure/scotthuang_invalid_structure_type.json"
        );
    }

    @Test
    void testMissingMandatoryJsonStructure() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeValidateError(
                "../yang/scotthuang-structure",
                "../data/structure/scotthuang_invalid_structure_missing_mandatory.json"
        );
    }

    @Test
    void testInvalidStructureTopLevelPrefix() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/scotthuang-structure",
                "../data/structure/scotthuang_invalid_structure_top_level_prefix.json"
        );
    }

    @Test
    void testInvalidStructureUnknownLeafInMetadata() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/scotthuang-structure",
                "../data/structure/scotthuang_invalid_structure_unknown_leaf.json"
        );
    }

    @Test
    void testValidStructureSequenceNumberBoundaryMax() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/scotthuang-structure",
                "../data/structure/scotthuang_invalid_structure_boundary_max.json"
        );
    }

    @Test
    void testInvalidStructureSequenceNumberOutOfRange() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/scotthuang-structure",
                "../data/structure/scotthuang_invalid_structure_out_of_range.json"
        );
    }

    @Test
    void testInvalidStructureTimestampFormat() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/scotthuang-structure",
                "../data/structure/scotthuang_invalid_structure_invalid_timestamp_format.json"
        );
    }

    @Test
    void testInvalidStructureTimestampValue() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/scotthuang-structure",
                "../data/structure/scotthuang_invalid_structure_invalid_timestamp_value.json"
        );
    }
}