import org.junit.jupiter.api.Test;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.ContainerNode;
import org.opendaylight.yangtools.yang.data.api.schema.LeafNode;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.spi.node.ImmutableNodes;
import org.opendaylight.yangtools.yang.data.tree.api.DataTree;
import org.opendaylight.yangtools.yang.data.tree.api.DataTreeModification;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YangTransformationTest {

    private static final String NS = "urn:yang:transformation";

    private static QName qname(String localName) {
        return QName.create(NS, localName).intern();
    }

    private static YangInstanceIdentifier.NodeIdentifier nodeId(QName qname) {
        return new YangInstanceIdentifier.NodeIdentifier(qname);
    }

    private static DataTree initDataTree(
            EffectiveModelContext schemaContext,
            YangInstanceIdentifier rootPath,
            NormalizedNode rootNode
    ) throws Exception {
        DataTree dataTree = YangToolsUtils.newOperationalTree(schemaContext);

        DataTreeModification initMod = dataTree.takeSnapshot().newModification();
        initMod.write(rootPath, rootNode);
        initMod.ready();

        dataTree.validate(initMod);
        dataTree.commit(dataTree.prepare(initMod));

        return dataTree;
    }

    private static LeafNode<?> writeLeafAndRead(
            DataTree dataTree,
            YangInstanceIdentifier leafPath,
            QName leafQName,
            Object value
    ) throws Exception {
        var leaf = ImmutableNodes.leafNode(leafQName, value);

        DataTreeModification updateMod = dataTree.takeSnapshot().newModification();
        updateMod.write(leafPath, leaf);
        updateMod.ready();

        dataTree.validate(updateMod);
        dataTree.commit(dataTree.prepare(updateMod));

        NormalizedNode readNode = dataTree.takeSnapshot()
                .readNode(leafPath)
                .orElseThrow();

        return assertInstanceOf(LeafNode.class, readNode);
    }

    @Test
    void addValueTest() throws Exception {
        EffectiveModelContext schemaContext = YangToolsUtils.loadValidSchema(
                "../yang/yang-transformation"
        );

        NormalizedNode rootNode = YangToolsUtils.loadValidNormalizedNode(
                schemaContext,
                "../data/yang-transformation/yang_transformation_bis.json"
        );

        assertNotNull(rootNode);

        System.out.println("=== Parsed JSON ===");
        YangToolsUtils.printDataTree(rootNode);

        ContainerNode fooNode = assertInstanceOf(ContainerNode.class, rootNode);

        QName fooQName = qname("foo");
        QName numQName = qname("num");

        YangInstanceIdentifier fooPath = YangInstanceIdentifier.of(nodeId(fooQName));
        YangInstanceIdentifier numPath = fooPath.node(nodeId(numQName));

        DataTree dataTree = initDataTree(schemaContext, fooPath, fooNode);

        LeafNode<?> leafNode = writeLeafAndRead(
                dataTree,
                numPath,
                numQName,
                3
        );

        assertEquals(3, leafNode.body());

        System.out.println("=== DataTree after update ===");
        NormalizedNode updatedFoo = dataTree.takeSnapshot()
                .readNode(fooPath)
                .orElseThrow();

        YangToolsUtils.printDataTree(updatedFoo);
    }

    @Test
    void updateValueTest() throws Exception {
        EffectiveModelContext schemaContext = YangToolsUtils.loadValidSchema(
                "../yang/yang-transformation"
        );

        NormalizedNode rootNode = YangToolsUtils.loadValidNormalizedNode(
                schemaContext,
                "../data/yang-transformation/yang_transformation_bis.json"
        );

        assertNotNull(rootNode);

        System.out.println("=== Parsed JSON ===");
        YangToolsUtils.printDataTree(rootNode);

        ContainerNode fooNode = assertInstanceOf(ContainerNode.class, rootNode);

        QName fooQName = qname("foo");
        QName valueToUpdateQName = qname("value-to-update");

        YangInstanceIdentifier fooPath = YangInstanceIdentifier.of(nodeId(fooQName));
        YangInstanceIdentifier valueToUpdatePath = fooPath.node(nodeId(valueToUpdateQName));

        DataTree dataTree = initDataTree(schemaContext, fooPath, fooNode);

        LeafNode<?> leafNode = writeLeafAndRead(
                dataTree,
                valueToUpdatePath,
                valueToUpdateQName,
                3
        );

        assertEquals(3, leafNode.body());

        System.out.println("=== DataTree after update ===");
        NormalizedNode updatedFoo = dataTree.takeSnapshot()
                .readNode(fooPath)
                .orElseThrow();

        YangToolsUtils.printDataTree(updatedFoo);
    }
}