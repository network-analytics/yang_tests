import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.yangcentral.yangkit.common.api.QName;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.model.api.stmt.Module;
import org.yangcentral.yangkit.parser.YangParserException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ImportOnlyModuleTest {

    private static final String YANG_DIRECTORY =
            "../yang/import-only";

    private static final String VALID_APPLICATION_DATA =
            "../data/import-only/application-valid.json";

    private static final String INVALID_APPLICATION_PORT =
            "../data/import-only/application-invalid-port.json";

    private static final String IMPORT_ONLY_MODULE_DATA =
            "../data/import-only/shared-types-data.json";

    private static final String SHARED_MODULE_NAME =
            "shared-types";

    private static final String SHARED_MODULE_REVISION =
            "2026-07-18";

    private static final QName SHARED_ROOT = new QName(
            "urn:test:shared-types",
            "shared-root"
    );

    private static final QName APPLICATION_ROOT = new QName(
            "urn:test:application",
            "application-root"
    );

    @Test
    void testNormalModuleExposesItsDataNodes()
            throws DocumentException, IOException, YangParserException {

        YangSchemaContext context =
                YangkitUtils.loadValidSchema(YANG_DIRECTORY);

        assertEquals(
                2,
                context.getModules().size()
        );

        assertEquals(
                0,
                context.getImportOnlyModules().size()
        );

        assertNotNull(
                context.getDataNodeChild(SHARED_ROOT),
                "shared-root should be exposed when shared-types is normal"
        );

        assertNotNull(
                context.getDataNodeChild(APPLICATION_ROOT),
                "application-root should be exposed"
        );
    }

    @Test
    void testImportOnlyModuleDoesNotExposeItsDataNodes()
            throws DocumentException, IOException, YangParserException {

        YangSchemaContext context =
                YangkitUtils.loadValidSchemaWithImportOnly(
                        YANG_DIRECTORY,
                        SHARED_MODULE_NAME,
                        SHARED_MODULE_REVISION
                );

        assertEquals(
                1,
                context.getModules().size(),
                "Only application should remain an implemented module"
        );

        assertEquals(
                1,
                context.getImportOnlyModules().size(),
                "shared-types should be import-only"
        );

        Module sharedModule = context
                .getModule(
                        SHARED_MODULE_NAME,
                        SHARED_MODULE_REVISION
                )
                .orElseThrow();

        assertTrue(
                context.isImportOnly(sharedModule),
                "shared-types should be marked as import-only"
        );

        assertNull(
                context.getDataNodeChild(SHARED_ROOT),
                "shared-root must not be exposed by an import-only module"
        );

        assertNotNull(
                context.getDataNodeChild(APPLICATION_ROOT),
                "application-root must remain exposed"
        );
    }

    @Test
    void testImportOnlyTypedefCanStillBeUsed()
            throws DocumentException, IOException, YangParserException {

        YangSchemaContext context =
                YangkitUtils.loadValidSchemaWithImportOnly(
                        YANG_DIRECTORY,
                        SHARED_MODULE_NAME,
                        SHARED_MODULE_REVISION
                );

        YangkitUtils.loadValidYangDataDoc(
                context,
                VALID_APPLICATION_DATA
        );
    }

    @Test
    void testImportOnlyTypedefValidationIsStillApplied()
            throws DocumentException, IOException, YangParserException {

        YangSchemaContext context =
                YangkitUtils.loadValidSchemaWithImportOnly(
                        YANG_DIRECTORY,
                        SHARED_MODULE_NAME,
                        SHARED_MODULE_REVISION
                );

        YangkitUtils.loadInvalidYangDataDocParseError(
                context,
                INVALID_APPLICATION_PORT
        );
    }

    @Test
    void testImportOnlyModuleDataCannotBeParsed()
            throws DocumentException, IOException, YangParserException {

        YangSchemaContext context =
                YangkitUtils.loadValidSchemaWithImportOnly(
                        YANG_DIRECTORY,
                        SHARED_MODULE_NAME,
                        SHARED_MODULE_REVISION
                );

        YangkitUtils.loadInvalidYangDataDocParseError(
                context,
                IMPORT_ONLY_MODULE_DATA
        );
    }
}