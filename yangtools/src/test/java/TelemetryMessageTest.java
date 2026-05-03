import org.junit.jupiter.api.Test;

public class TelemetryMessageTest {

    @Test
    void testValidTelemetryMsgNetGauze() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/telemetry",
                "../data/telemetry/valid-telemetry-msg-netgauze.json"
        );
    }

    @Test
    void testValidTelemetryMsgPMACCT() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/telemetry",
                "../data/telemetry/valid-telemetry-msg-pmacct.json"
        );
    }

    @Test
    void testInvalidTelemetryMsg() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/telemetry",
                "../data/telemetry/invalid-telemetry-msg.json"
        );
    }
}