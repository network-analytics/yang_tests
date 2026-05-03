import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.yangcentral.yangkit.parser.YangParserException;

import java.io.IOException;

public class TelemetryMessageTest {

    @Test
    void testValidTelemetryMsgNetGauze() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadValidYangDataDoc("../yang/telemetry",
                "../data/telemetry/valid-telemetry-msg-netgauze.json");
    }

    @Test
    void testValidTelemetryMsgPMACCT() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadValidYangDataDoc("../yang/telemetry",
                "../data/telemetry/valid-telemetry-msg-pmacct.json");
    }

    @Test
    void testInvalidTelemetryMsg() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadInvalidYangDataDocParseError("../yang/telemetry",
                "../data/telemetry/invalid-telemetry-msg.json");
    }

}
