import org.junit.jupiter.api.Test;
import org.opendaylight.yangtools.yang.common.Revision;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YangLibraryTest {

    @Test
    void testYangLibraryWithEnabledIfFeature() throws Exception {
        EffectiveModelContext bootstrapContext = YangToolsUtils.loadValidSchema(
                "../yang/yang-library"
        );

        NormalizedNode yangLibraryDocument = YangToolsUtils.loadValidNormalizedNode(
                bootstrapContext,
                "../data/yang-library/yang-library-with-feature.json"
        );

        EffectiveModelContext generatedContext =
                YangLibraryUtils.newContextFromYangLibraryData(
                        Path.of("../yang/yang-library"),
                        yangLibraryDocument
                );

        assertNotNull(generatedContext);

        assertTrue(
                generatedContext
                        .findModule("feature-test", Revision.of("2026-04-29"))
                        .isPresent()
        );

        YangToolsUtils.loadValidNormalizedNode(
                generatedContext,
                "../data/yang-library/valid-feature-data.json"
        );
    }

    @Test
    void testYangLibraryWithoutIfFeatureShouldRejectFeatureLeaf() throws Exception {
        EffectiveModelContext bootstrapContext = YangToolsUtils.loadValidSchema(
                "../yang/yang-library"
        );

        NormalizedNode yangLibraryDocument = YangToolsUtils.loadValidNormalizedNode(
                bootstrapContext,
                "../data/yang-library/yang-library-without-feature.json"
        );

        EffectiveModelContext generatedContext =
                YangLibraryUtils.newContextFromYangLibraryData(
                        Path.of("../yang/yang-library"),
                        yangLibraryDocument
                );

        assertNotNull(generatedContext);

        YangToolsUtils.loadInvalidNormalizedNodeParseError(
                generatedContext,
                "../data/yang-library/valid-feature-data.json"
        );
    }
}