import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.yangcentral.yangkit.parser.YangParserException;

import java.io.IOException;

public class DataValidationJsonEncodingTest {

    @Test
    void testValidJsonValidation() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadValidYangDataDoc("../yang/data-validation/data-validation-test.yang",
                "../data/data-validation/data-validation-test-valid.json");
    }

    @Test
    void testInvalidJsonValidation() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadInvalidYangDataDocParseError("../yang/data-validation/data-validation-test.yang",
                "../data/data-validation/data-validation-test-invalid-string-validation.json");
    }

    @Test
    void testHostnameNumericValidation() throws Exception {
        YangkitUtils.loadInvalidYangDataDocParseError("../yang/data-validation/data-validation-test.yang",
                "../data/data-validation/data-validation-test-invalid-num-validation.json");
    }

    @Test
    void testPortOutOfBoundsValidation() throws Exception {
        YangkitUtils.loadInvalidYangDataDocParseError("../yang/data-validation/data-validation-test.yang",
                "../data/data-validation/data-validation-test-invalid-out-of-bounds.json");

    }

    @Test
    void testMissingMandatoryValidation() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadInvalidYangDataDocValidateError("../yang/mandatory-validation/mandatory-test.yang",
                "../data/mandatory-validation/missing.json");
    }

    @Test
    void testValidMustValidation() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadValidYangDataDoc("../yang/must-validation/must-test.yang",
                "../data/must-validation/valid-must.json");
    }

    @Test
    void testValidMustValidation2() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadValidYangDataDoc("../yang/must-validation/must-test.yang",
                "../data/must-validation/valid-must-2.json");
    }

    @Test
    void testInvalidMustValidation() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadInvalidYangDataDocValidateError("../yang/must-validation/must-test.yang",
                "../data/must-validation/invalid-must.json");
    }

}
