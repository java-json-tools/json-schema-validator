package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.CombinedValidator;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.jsonschema.validators.errors.IllegalSchemaValidator;
import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AbstractValidatorProvider
    implements ValidatorProvider
{
    protected final String nodeType;
    protected final JsonNode schemaNode;
    protected final Class<? extends Validator> typeValidator;
    protected final List<Class<? extends Validator>> validatorList
        = new LinkedList<Class<? extends Validator>>();

    AbstractValidatorProvider(final JsonNode schemaNode, final String nodeType,
        final Class<? extends Validator> typeValidator)
    {
        this.nodeType = nodeType;

        final Map<String, JsonNode> fields
            = CollectionUtils.toMap(schemaNode.getFields());

        fields.remove("type");
        final JsonNodeFactory factory = new ObjectMapper().getNodeFactory();
        fields.put("type", factory.textNode(nodeType));

        this.schemaNode = factory.objectNode().putAll(fields);
        this.typeValidator = typeValidator;
        validatorList.add(typeValidator);

    }

    @Override
    public final Validator getValidator()
    {
        final LinkedList<Validator> validators
            = new LinkedList<Validator>();

        Validator v;
        Constructor<? extends Validator> constructor;

        for (final Class<? extends Validator> c: validatorList) {
            try {
                constructor = c.getConstructor(JsonNode.class);
                v = constructor.newInstance(schemaNode);
                validators.add(v);
            } catch (Exception e) {
                return new IllegalSchemaValidator(e);
            }
        }
        return new CombinedValidator(validators);
    }

}
