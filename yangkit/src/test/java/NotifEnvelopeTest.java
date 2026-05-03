import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.yangcentral.yangkit.parser.YangParserException;

import java.io.IOException;

public class NotifEnvelopeTest {

    @Test
    void testValidNotifEnvelope() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadValidYangDataDoc("../yang/notif",
                "../data/notif/valid-notification.json");
    }

    @Test
    void testInvalidNotifEnvelope() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadInvalidYangDataDocParseError("../yang/notif",
                "../data/notif/invalid-notification.json");
    }
}
