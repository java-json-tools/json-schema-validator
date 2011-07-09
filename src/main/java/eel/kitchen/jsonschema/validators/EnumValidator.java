package eel.kitchen.jsonschema.validators;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;

import java.util.Collection;

public class EnumValidator
    extends AbstractValidator
{
    private Collection<JsonNode> values;

    public EnumValidator(final JsonNode schemaNode)
    {
        super(schemaNode);
    }

    @Override
    public void setup() throws MalformedJasonSchemaException
    {
        final JsonNode node = schemaNode.get("enum");

        if (!node.isArray())
            throw new MalformedJasonSchemaException("enum is not an array");

        try {
            values = CollectionUtils.toSet(node.getElements(), false);
        } catch (IllegalArgumentException e) {
            throw new MalformedJasonSchemaException("enum has duplicate values");
        }
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        final boolean ret = values.contains(node);

        if (!ret)
            validationErrors.add("value does not match any member in the "
                + "enumeration");

        return ret;
    }
}
