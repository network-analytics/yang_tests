import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dom4j.DocumentException;
import org.yangcentral.yangkit.common.api.QName;
import org.yangcentral.yangkit.common.api.validate.ValidatorResult;
import org.yangcentral.yangkit.common.api.validate.ValidatorResultBuilder;
import org.yangcentral.yangkit.data.api.model.LeafData;
import org.yangcentral.yangkit.data.api.model.YangDataDocument;
import org.yangcentral.yangkit.data.codec.json.NotificationMessageJsonCodec;
import org.yangcentral.yangkit.data.codec.json.YangDataDocumentJsonParser;
import org.yangcentral.yangkit.data.impl.model.LeafDataImpl;
import org.yangcentral.yangkit.data.impl.model.SingleInstanceDataIdentifier;
import org.yangcentral.yangkit.model.api.codec.YangCodecException;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.parser.YangParserException;
import org.yangcentral.yangkit.parser.YangYinParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class YangkitUtils {
    public static YangSchemaContext loadSchema(String yangFiles) throws DocumentException, IOException, YangParserException {
        var f = Paths.get(yangFiles).toAbsolutePath();
        return YangYinParser.parse(f.toFile());
    }

    public static JsonNode loadJson(String jsonFile){
        JsonNode data = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            data = objectMapper.readTree(new File(jsonFile));
        } catch (IOException ignored) {
        }
        return  data;
    }

    public static ValidatorResult validateSchema(YangSchemaContext schema){
        return schema.validate();
    }

    public static ValidatorResult parsingData(YangSchemaContext schema, JsonNode data){
        ValidatorResultBuilder validatorResultBuilder = new ValidatorResultBuilder();
        YangDataDocument yangDataDocument = new YangDataDocumentJsonParser(schema).parse(data, validatorResultBuilder);
        return validatorResultBuilder.build();
    }

    public static ValidatorResult validateData(YangSchemaContext schema, JsonNode data){
        ValidatorResultBuilder tmp = new ValidatorResultBuilder();
        YangDataDocument yangDataDocument = new YangDataDocumentJsonParser(schema).parse(data, tmp);
        yangDataDocument.update();
        return yangDataDocument.validate();
    }

    public static void getTree(Object node, String indent) throws YangCodecException {
        if (!(node instanceof org.yangcentral.yangkit.data.api.model.YangData<?> data)) {
            System.out.println(indent + node);
            return;
        }

        if(node instanceof LeafDataImpl<?>){
            System.out.println(indent + data.getQName().getQualifiedName() + " -> {" + ((LeafDataImpl) node).getValue().getValue().toString() + "} (" + data.getClass() + ")");
        }else{
            System.out.println(indent + data.getQName().getQualifiedName() + " -> (" + data.getClass() + ")");
        }


        if (data instanceof org.yangcentral.yangkit.data.api.model.YangDataContainer container) {
            for (var child : container.getDataChildren()) {
                getTree(child, indent + "  ");
            }
        }
    }

    public static SingleInstanceDataIdentifier getIdentifier(String namespace, String localName){
        QName qname = new QName(namespace,localName);
        return new SingleInstanceDataIdentifier(qname);
    }

    public static void debugDependencyTest(YangSchemaContext schemaContext) throws DocumentException, IOException, YangParserException {
        var validation = schemaContext.validate();
        for(var r : validation.getRecords()){
            if(r.getSeverity().toString().equals("ERROR")){
                System.out.println(r.getBadElement() + " " + r.getErrorMsg().getMessage());
            }
        }
        assertTrue(validation.isOk());
    }
}