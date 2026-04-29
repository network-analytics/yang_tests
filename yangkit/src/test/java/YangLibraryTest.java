import com.fasterxml.jackson.databind.JsonNode;
import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.yangcentral.yangkit.common.api.validate.ValidatorResult;
import org.yangcentral.yangkit.common.api.validate.ValidatorResultBuilder;
import org.yangcentral.yangkit.data.api.model.YangDataDocument;
import org.yangcentral.yangkit.data.codec.json.YangDataDocumentJsonParser;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.parser.YangParserException;

import static org.junit.jupiter.api.Assertions.*;


import java.io.IOException;
import java.nio.file.Path;

public class YangLibraryTest {

    @Test
    void testYangLibraryWithEnabledIfFeature() throws Exception {
        YangSchemaContext bootstrapContext = YangkitUtils.loadSchema("../yang/yang-library");
        ValidatorResult schemaValidation = YangkitUtils.validateSchema(bootstrapContext);
        assertTrue(schemaValidation.isOk());

        JsonNode yangLibraryJson =
                YangkitUtils.loadJson("../data/yang-library-with-feature.json");

        YangDataDocument yangLibraryDocument =
                parseYangDataDocument(bootstrapContext, yangLibraryJson);

        YangSchemaContext generatedContext =
                YangLibrary.newContextFromYangLibraryData(
                        Path.of("../yang/yang-library"),
                        yangLibraryDocument
                );

        ValidatorResult generatedSchemaValidation =
                YangkitUtils.validateSchema(generatedContext);
        assertTrue(generatedSchemaValidation.isOk());

        assertTrue(
                generatedContext
                        .getModule("feature-test", "2026-04-29")
                        .isPresent()
        );

        JsonNode validFeatureData =
                YangkitUtils.loadJson("../data/valid-feature-data.json");

        ValidatorResult firstDataValidation =
                YangkitUtils.parsingData(generatedContext, validFeatureData);
        assertTrue(firstDataValidation.isOk());

        ValidatorResult secondDataValidation =
                YangkitUtils.validateData(generatedContext, validFeatureData);
        assertTrue(secondDataValidation.isOk());
    }

    @Test
    void testYangLibraryWithoutIfFeatureShouldRejectFeatureLeaf() throws Exception {
        YangSchemaContext bootstrapContext = YangkitUtils.loadSchema("../yang/yang-library");
        ValidatorResult schemaValidation = YangkitUtils.validateSchema(bootstrapContext);
        assertTrue(schemaValidation.isOk());

        JsonNode yangLibraryJson =
                YangkitUtils.loadJson("../data/yang-library-without-feature.json");

        YangDataDocument yangLibraryDocument =
                parseYangDataDocument(bootstrapContext, yangLibraryJson);

        YangSchemaContext generatedContext =
                YangLibrary.newContextFromYangLibraryData(
                        Path.of("../yang/yang-library"),
                        yangLibraryDocument
                );

        ValidatorResult generatedSchemaValidation =
                YangkitUtils.validateSchema(generatedContext);
        assertTrue(generatedSchemaValidation.isOk());

        JsonNode dataWithFeatureLeaf =
                YangkitUtils.loadJson("../data/valid-feature-data.json");

        boolean rejected;

        try {
            ValidatorResult firstDataValidation =
                    YangkitUtils.parsingData(generatedContext, dataWithFeatureLeaf);

            rejected = !firstDataValidation.isOk();
        } catch (Exception e) {
            rejected = true;
        }

        assertTrue(
                rejected
        );
    }

    private static YangDataDocument parseYangDataDocument(
            YangSchemaContext schemaContext,
            JsonNode jsonNode
    ) {
        ValidatorResultBuilder builder = new ValidatorResultBuilder();

        YangDataDocument document =
                new YangDataDocumentJsonParser(schemaContext)
                        .parse(jsonNode, builder);

        ValidatorResult parsingResult = builder.build();
        assertTrue(
                parsingResult.isOk()
        );

        document.update();

        ValidatorResult validationResult = document.validate();
        assertTrue(
                validationResult.isOk()
        );

        return document;
    }

}
