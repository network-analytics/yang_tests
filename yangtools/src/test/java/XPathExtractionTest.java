import org.junit.jupiter.api.Test;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.ContainerNode;
import org.opendaylight.yangtools.yang.data.api.schema.LeafNode;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNodes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class XPathExtractionTest {

    private static YangInstanceIdentifier.NodeIdentifier node(String namespace, String localName) {
        return YangInstanceIdentifier.NodeIdentifier.create(
                QName.create(namespace, localName).intern()
        );
    }

    private static String stringValueOrEmpty(NormalizedNode root, YangInstanceIdentifier path) {
        Optional<NormalizedNode> result = NormalizedNodes.findNode(root, path);

        if (result.isEmpty()) {
            return "";
        }

        NormalizedNode node = result.get();

        if (!(node instanceof LeafNode<?> leafNode)) {
            return "";
        }

        Object value = leafNode.body();
        return value == null ? "" : value.toString();
    }

    @Test
    void minimalXpathTest() throws Exception {
        NormalizedNode root = YangToolsUtils.loadValidNormalizedNode(
                "../yang/xpath",
                "../data/xpath/yangtools-xpath-test-valid.json"
        );

        String ns = "urn:xpath:test";

        YangInstanceIdentifier path = YangInstanceIdentifier.of(
                node(ns, "child-container"),
                node(ns, "value1")
        );

        String value = stringValueOrEmpty(root, path);

        assertEquals("value", value);
    }

    @Test
    void minimalContainerXpathTest() throws Exception {
        NormalizedNode root = YangToolsUtils.loadValidNormalizedNode(
                "../yang/xpath",
                "../data/xpath/yangtools-xpath-test-valid.json"
        );

        String ns = "urn:xpath:test";

        YangInstanceIdentifier path = YangInstanceIdentifier.of(
                node(ns, "child-container")
        );

        Optional<NormalizedNode> result = NormalizedNodes.findNode(root, path);

        assertTrue(result.isPresent(), "Node not found");
        assertInstanceOf(ContainerNode.class, result.get());
    }

    @Test
    void anydataXpathTest() throws Exception {
        NormalizedNode root = YangToolsUtils.loadValidNormalizedNode(
                "../yang/xpath",
                "../data/xpath/anydata-xpath-test-valid.json"
        );

        String xt = "urn:xpath:test";
        String st = "urn:schema:test";

        YangInstanceIdentifier path = YangInstanceIdentifier.of(
                node(xt, "child-container"),
                node(xt, "payload"),
                node(st, "system"),
                node(st, "hostname")
        );

        String value = stringValueOrEmpty(root, path);

        assertEquals("router1", value);
    }

    @Test
    void structureXpathTest() throws Exception {
        NormalizedNode root = YangToolsUtils.loadValidNormalizedNode(
                "../yang/xpath-structure",
                "../data/xpath-structure/scotthuang_valid_structure_message.json"
        );

        String ts = "urn:example:test-structure";

        YangInstanceIdentifier path = YangInstanceIdentifier.of(
                node(ts, "content")
        );

        String value = stringValueOrEmpty(root, path);

        assertEquals("sample data", value);
    }

    @Test
    void nonExistentXpathTest() throws Exception {
        NormalizedNode root = YangToolsUtils.loadValidNormalizedNode(
                "../yang/xpath-structure",
                "../data/xpath-structure/non-existent-xpath.json"
        );

        String ts = "urn:example:test-structure";

        YangInstanceIdentifier path = YangInstanceIdentifier.of(
                node(ts, "metadata"),
                node(ts, "non-existent-leaf")
        );

        String value = stringValueOrEmpty(root, path);

        assertEquals("", value, "Extracting a non-existent node should return an empty string");
    }
}