import org.junit.jupiter.api.Test;

public class YangPushTest {

    @Test
    void test1ValidPushUpdate() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/yangpush",
                "../data/yangpush/1-push-update.json"
        );
    }

    @Test
    void test1ValidSubscriptionStarted() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/yangpush",
                "../data/yangpush/1-subscription-started.json"
        );
    }

    @Test
    void test2ValidPushUpdate() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/yangpush",
                "../data/yangpush/2-push-update.json"
        );
    }

    @Test
    void test2ValidSubscriptionStarted() throws Exception {
        YangToolsUtils.loadValidNormalizedNode(
                "../yang/yangpush",
                "../data/yangpush/2-subscription-started.json"
        );
    }
}