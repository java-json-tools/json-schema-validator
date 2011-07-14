package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.CombinedValidator;
import eel.kitchen.jsonschema.validators.EnumValidator;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.jsonschema.validators.errors.AlwaysFalseValidator;
import eel.kitchen.jsonschema.validators.format.FormatValidator;
import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractValidatorProvider
    implements ValidatorProvider
{
    protected final String nodeType;
    protected JsonNode schema;
    protected final Class<? extends Validator> typeValidator;
    protected final List<Class<? extends Validator>> validatorList
        = new LinkedList<Class<? extends Validator>>();
    protected boolean hasEnum, hasFormat;

    protected AbstractValidatorProvider(final String nodeType,
        final Class<? extends Validator> typeValidator,
        final boolean hasEnum, final boolean hasFormat)
    {
        this.nodeType = nodeType;

        this.typeValidator = typeValidator;
        validatorList.add(typeValidator);

        this.hasEnum = hasEnum;
        this.hasFormat = hasFormat;
    }

    protected AbstractValidatorProvider(final JsonNode schema,
        final String nodeType, final Class<? extends Validator> typeValidator,
        final boolean hasEnum, final boolean hasFormat)
    {
        this(nodeType, typeValidator, hasEnum, hasFormat);
        setSchema(schema);
    }

    @Override
    public final void setSchema(final JsonNode schema)
    {
        final Map<String, JsonNode> fields
            = CollectionUtils.toMap(schema.getFields());

        fields.remove("type");
        final JsonNodeFactory factory = new ObjectMapper().getNodeFactory();
        fields.put("type", factory.textNode(nodeType));

        this.schema = factory.objectNode().putAll(fields);

        if (hasEnum && schema.has("enum"))
            validatorList.add(EnumValidator.class);
        if (hasFormat && schema.has("format"))
            validatorList.add(FormatValidator.class);
    }

    @Override
    public final Validator getValidator()
    {
        final LinkedList<Validator> validators
            = new LinkedList<Validator>();

        Validator v;

        for (final Class<? extends Validator> c: validatorList) {
            try {
                v = c.getConstructor().newInstance();
            } catch (Exception e) {
                return new AlwaysFalseValidator(String.format("cannot "
                    + "instantiate validator: %s: %s",
                    e.getClass().getCanonicalName(), e.getMessage()));
            }
            v.setSchema(schema);
            validators.add(v);
        }
        return new CombinedValidator(validators);
    }

}
