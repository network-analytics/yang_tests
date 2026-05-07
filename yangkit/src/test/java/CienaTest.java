import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.yangcentral.yangkit.parser.YangParserException;

import java.io.IOException;

public class CienaTest {

    //4
    //7_3

    @Test
    void test1() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-yangpush-receiver-yang/9ee10ed0e4aea47109a0688a6500d903f3c95a2c46f274da04fa909d173e8aab/modules",
                "../data/ciena-messagebroker-consumer/fano-yp-s1-push-update.json");
    }

    @Test
    void test2() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-yangpush-receiver-yang/999922077db592666e46f664c2328392b6edf932dd3ead723c85b42ee8941eac",
                "../data/ciena-messagebroker-consumer/fano-yp-s3-push-update.json");
    }

    @Test
    void test3_0() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-yangpush-receiver-yang/b7dd215db97eae1a19baaa5c880823d8d3174a9ba075b4e47260066d7060ddbd",
                "../data/ciena-messagebroker-consumer/fano-yp-s6-push-update.json");
    }

    @Test
    void test3_1() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-yangpush-receiver-yang/b7dd215db97eae1a19baaa5c880823d8d3174a9ba075b4e47260066d7060ddbd",
                "../data/ciena-messagebroker-consumer/fano-yp-s7-push-update.json");
    }

    @Test
    void test3_2() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-yangpush-receiver-yang/b7dd215db97eae1a19baaa5c880823d8d3174a9ba075b4e47260066d7060ddbd",
                "../data/ciena-messagebroker-consumer/fano-yp-s8-push-update.json");
    }

    @Test
    void test3_3() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-yangpush-receiver-yang/b7dd215db97eae1a19baaa5c880823d8d3174a9ba075b4e47260066d7060ddbd",
                "../data/ciena-messagebroker-consumer/fano-yp-s10-push-update.json");
    }

    @Test
    void test3_4() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-yangpush-receiver-yang/b7dd215db97eae1a19baaa5c880823d8d3174a9ba075b4e47260066d7060ddbd",
                "../data/ciena-messagebroker-consumer/fano-yp-s11-push-update.json");
    }

    @Test
    void test4() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-yangpush-receiver-yang/64b1f735c235c057215a7bd723023c2771693b1306bb6500968b70226228ee78",
                "../data/ciena-messagebroker-consumer/fano-yp-s9-push-update.json");
    }

    @Test
    void test5() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-yangpush-receiver-yang/883bba6ecbe5bc27f7b9a2a4b17a14e1d78471a74f65ee473abaae5e3c0dd6b9",
                "../data/ciena-messagebroker-consumer/fano-yp-s12-push-update.json");
    }

    @Test
    void test6_0() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-messagebroker-consumer-yang/schema-id-230966",
                "../data/ciena-messagebroker-consumer/fano-mb-yp-s1-push-update.json");
    }

    @Test
    void test6_1() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-messagebroker-consumer-yang/schema-id-230966",
                "../data/ciena-messagebroker-consumer/fano-mb-yp-s3-push-update.json");
    }

    @Test
    void test6_2() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-messagebroker-consumer-yang/schema-id-230966",
                "../data/ciena-messagebroker-consumer/fano-mb-yp-s12-push-update.json");
    }

    @Test
    void test7_0() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-messagebroker-consumer-yang/schema-id-230968",
                "../data/ciena-messagebroker-consumer/fano-mb-yp-s6-push-update.json");
    }

    @Test
    void test7_1() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-messagebroker-consumer-yang/schema-id-230968",
                "../data/ciena-messagebroker-consumer/fano-mb-yp-s7-push-update.json");
    }

    @Test
    void test7_2() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-messagebroker-consumer-yang/schema-id-230968",
                "../data/ciena-messagebroker-consumer/fano-mb-yp-s8-push-update.json");
    }

    @Test
    void test7_3() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-messagebroker-consumer-yang/schema-id-230968",
                "../data/ciena-messagebroker-consumer/fano-mb-yp-s9-push-update.json");
    }

    @Test
    void test7_4() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-messagebroker-consumer-yang/schema-id-230968",
                "../data/ciena-messagebroker-consumer/fano-mb-yp-s10-push-update.json");
    }

    @Test
    void test7_5() throws DocumentException, IOException, YangParserException {
        YangkitUtils.loadYangDataDocSkipSchemaValidation("../yang/netgauze-messagebroker-consumer-yang/schema-id-230968",
                "../data/ciena-messagebroker-consumer/fano-mb-yp-s11-push-update.json");
    }

}