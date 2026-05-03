import org.junit.jupiter.api.Test;

public class NotifEnvelopeTest {

    @Test
    void testValidNotifEnvelope() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/notif",
                "../data/notif/valid-notification.json"
        );
    }

    @Test
    void testInvalidNotifEnvelope() throws Exception {
        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                "../yang/notif",
                "../data/notif/invalid-notification.json"
        );
    }
}