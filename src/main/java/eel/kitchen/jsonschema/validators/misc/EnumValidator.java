package eel.kitchen.jsonschema.validators.misc;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.util.JasonHelper;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public final class EnumValidator
    extends AbstractValidator
{
    private static final Map<String, EnumSet<NodeType>> FIELDS
        = new LinkedHashMap<String, EnumSet<NodeType>>();

    private final Collection<JsonNode> values = new HashSet<JsonNode>();

    public EnumValidator()
    {
        registerField("enum", NodeType.ARRAY);
    }

    @Override
    protected Map<String, EnumSet<NodeType>> fieldMap()
    {
        return FIELDS;
    }

    @Override
    protected boolean doSetup()
    {
        if (!super.doSetup())
            return false;

        final JsonNode node = schema.get("enum");
        final String expected = schema.get("type").getTextValue();

        for (final JsonNode element: node) {
            if (!expected.equals(JasonHelper.getNodeType(element)))
                continue;
            if (!values.add(element)) {
                messages.add("enum has duplicate values");
                return false;
            }
        }

        if (!values.isEmpty())
            return true;

        messages.add("no element in enum has expected type " + expected);
        return false;
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        if (!setup())
            return false;

        messages.clear();

        if (values.contains(node))
            return true;

        messages.add("node does not match any value in the enumeration");
        return false;
    }
}
