import org.junit.jupiter.api.Test;

import java.util.List;

public class DataValidationJsonEncodingTest {

    @Test
    void testValidJsonValidation() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                List.of("../yang/data-validation/data-validation-test.yang"),
                "../data/data-validation/data-validation-test-valid.json"
        );
    }

    @Test
    void testInvalidJsonValidation() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                List.of("../yang/data-validation/data-validation-test.yang"),
                "../data/data-validation/data-validation-test-invalid-string-validation.json"
        );
    }

    @Test
    void testHostnameNumericValidation() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                List.of("../yang/data-validation/data-validation-test.yang"),
                "../data/data-validation/data-validation-test-invalid-num-validation.json"
        );
    }

    @Test
    void testPortOutOfBoundsValidation() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                List.of("../yang/data-validation/data-validation-test.yang"),
                "../data/data-validation/data-validation-test-invalid-out-of-bounds.json"
        );
    }

    @Test
    void testMissingMandatoryValidation() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeValidateError(
                List.of("../yang/mandatory-validation/mandatory-test.yang"),
                "../data/mandatory-validation/missing.json"
        );
    }

    // todo check how to validate "must"
    @Test
    void testValidMustValidation() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                List.of("../yang/must-validation/must-test.yang"),
                "../data/must-validation/valid-must.json"
        );
    }

    @Test
    void testValidMustValidation2() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                List.of("../yang/must-validation/must-test.yang"),
                "../data/must-validation/valid-must-2.json"
        );
    }

    @Test
    void testInvalidMustValidation() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeValidateError(
                List.of("../yang/must-validation/must-test.yang"),
                "../data/must-validation/invalid-must.json"
        );
    }
}