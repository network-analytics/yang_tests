import com.fasterxml.jackson.databind.JsonNode;
import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.yangcentral.yangkit.common.api.validate.ValidatorResult;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.parser.YangParserException;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TelemetryMessageTest {

    @Test
    void testValidTelemetryMsgNetGauze() throws DocumentException, IOException, YangParserException {
        YangSchemaContext schemaContext = YangkitUtils.loadSchema("../yang/telemetry");
        JsonNode validData = YangkitUtils.loadJson("../data/valid-telemetry-msg-netgauze.json");
        ValidatorResult schemaValidation = YangkitUtils.validateSchema(schemaContext);
        assertTrue(schemaValidation.isOk());
        ValidatorResult firstDataValidation = YangkitUtils.parsingData(schemaContext, validData);
        assertTrue(firstDataValidation.isOk());
        ValidatorResult secondDataValidation = YangkitUtils.validateData(schemaContext, validData);
        assertTrue(secondDataValidation.isOk());
    }

    @Test
    void testValidTelemetryMsgPMACCT() throws DocumentException, IOException, YangParserException {
        YangSchemaContext schemaContext = YangkitUtils.loadSchema("../yang/telemetry");
        JsonNode validData = YangkitUtils.loadJson("../data/valid-telemetry-msg-pmacct.json");
        ValidatorResult schemaValidation = YangkitUtils.validateSchema(schemaContext);
        assertTrue(schemaValidation.isOk());
        ValidatorResult firstDataValidation = YangkitUtils.parsingData(schemaContext, validData);
        for(var r : firstDataValidation.getRecords()){
            if(r.getSeverity().toString().equals("ERROR")){
                System.out.println(r.getBadElement() + "--> " + r.getErrorMsg().getMessage());
            }
        }
        assertTrue(firstDataValidation.isOk());
        ValidatorResult secondDataValidation = YangkitUtils.validateData(schemaContext, validData);
        assertTrue(secondDataValidation.isOk());
    }

    @Test
    void testInvalidTelemetryMsg() throws DocumentException, IOException, YangParserException {
        YangSchemaContext schemaContext = YangkitUtils.loadSchema("../yang/telemetry");
        JsonNode validData = YangkitUtils.loadJson("../data/invalid-telemetry-msg.json");
        ValidatorResult schemaValidation = YangkitUtils.validateSchema(schemaContext);
        assertTrue(schemaValidation.isOk());
        assertThrows(Exception.class, () -> YangkitUtils.parsingData(schemaContext, validData));
    }

}
