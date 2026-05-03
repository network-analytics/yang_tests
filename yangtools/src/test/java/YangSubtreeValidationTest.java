import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.Test;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.ContainerNode;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;
import org.opendaylight.yangtools.yang.data.codec.gson.JsonParserStream;
import org.opendaylight.yangtools.yang.data.impl.schema.ImmutableNormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.impl.schema.NormalizationResultHolder;
import org.opendaylight.yangtools.yang.data.spi.node.MandatoryLeafEnforcer;
import org.opendaylight.yangtools.yang.data.tree.api.DataTreeModification;
import org.opendaylight.yangtools.yang.data.util.DataSchemaContextTree;
import org.opendaylight.yangtools.yang.model.api.DataNodeContainer;
import org.opendaylight.yangtools.yang.model.util.SchemaInferenceStack;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class YangSubtreeValidationTest {

    @Test
    void subtreeValidationTest() throws Exception {
        var schemaContext = YangToolsUtils.loadValidSchema(
                List.of("../yang/subtree/subtree.yang")
        );

        assertNotNull(schemaContext);

        QName rootQName = QName.create("urn:example", "root").intern();
        QName contentQName = QName.create("urn:example", "content").intern();
        QName subscriptionQName = QName.create("urn:example", "subscription").intern();

        var contentInference = SchemaInferenceStack
                .ofDataTreePath(schemaContext, rootQName, contentQName)
                .toInference();

        NormalizationResultHolder resultHolder = new NormalizationResultHolder();
        var writer = ImmutableNormalizedNodeStreamWriter.from(resultHolder);

        try (
                var inputStream = Files.newInputStream(
                        Paths.get("../data/subtree/subtree.json")
                );
                var reader = new JsonReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8)
                );
                var parser = JsonParserStream.create(
                        writer,
                        JSONCodecFactorySupplier.RFC7951.getShared(schemaContext),
                        contentInference
                )
        ) {
            parser.parse(reader);
        }

        assertNotNull(resultHolder.getResult(), "normalization result is null");

        ContainerNode subscriptionNode = assertInstanceOf(
                ContainerNode.class,
                resultHolder.getResult().data()
        );

        YangInstanceIdentifier subscriptionPath = YangInstanceIdentifier.of(
                new YangInstanceIdentifier.NodeIdentifier(rootQName)
        ).node(
                new YangInstanceIdentifier.NodeIdentifier(contentQName)
        ).node(
                new YangInstanceIdentifier.NodeIdentifier(subscriptionQName)
        );

        var dataTree = YangToolsUtils.newOperationalTree(schemaContext);

        DataTreeModification modification = dataTree.takeSnapshot().newModification();
        modification.write(subscriptionPath, subscriptionNode);
        modification.ready();

        dataTree.validate(modification);
        dataTree.commit(dataTree.prepare(modification));

        NormalizedNode readNode = dataTree.takeSnapshot()
                .readNode(subscriptionPath)
                .orElseThrow();

        ContainerNode validatedSubscription = assertInstanceOf(
                ContainerNode.class,
                readNode
        );

        var subscriptionSchemaContext = DataSchemaContextTree.from(schemaContext)
                .findChild(subscriptionPath)
                .orElseThrow();

        DataNodeContainer subscriptionSchema = assertInstanceOf(
                DataNodeContainer.class,
                subscriptionSchemaContext.dataSchemaNode()
        );

        MandatoryLeafEnforcer enforcer = MandatoryLeafEnforcer.forContainer(
                subscriptionSchema,
                true
        );

        assertNull(enforcer);

        System.out.println("=== Validated subtree ===");
        YangToolsUtils.printDataTree(validatedSubscription);
    }
}