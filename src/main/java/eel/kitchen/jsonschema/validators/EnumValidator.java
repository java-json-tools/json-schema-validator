package eel.kitchen.jsonschema.validators;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.type.AbstractTypeValidator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;

import java.util.Collection;
import java.util.HashSet;

public final class EnumValidator
    extends AbstractTypeValidator
{
    private final Collection<JsonNode> values = new HashSet<JsonNode>();

    public EnumValidator(final JsonNode schemaNode)
    {
        super(schemaNode);
    }

    @Override
    public void setup() throws MalformedJasonSchemaException
    {
        final JsonNode node = schemaNode.get("enum");
        final String expected = schemaNode.get("type").getTextValue();

        if (!node.isArray())
            throw new MalformedJasonSchemaException("enum is not an array");

        for (final JsonNode element: node) {
            if (!expected.equals(JasonHelper.getNodeType(element)))
                continue;
            if (!values.add(element))
                throw new MalformedJasonSchemaException("enum has duplicate "
                    + "values");
        }

        if (values.isEmpty())
            throw new MalformedJasonSchemaException("no element in the "
                + "enumeration has expected type " + expected);
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        validationErrors.clear();

        if (values.contains(node))
            return true;

        validationErrors.add("node does not match any value in the enumeration");
        return false;
    }
}
