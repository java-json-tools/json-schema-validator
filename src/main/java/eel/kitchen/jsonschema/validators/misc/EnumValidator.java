package eel.kitchen.jsonschema.validators.misc;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Collection;
import java.util.HashSet;

public final class EnumValidator
    extends AbstractValidator
{
    private final Collection<JsonNode> values = new HashSet<JsonNode>();
    private boolean voidEnum = false;

    public EnumValidator()
    {
        registerField("enum", NodeType.ARRAY);
    }

    @Override
    protected boolean doSetup()
    {
        if (!super.doSetup())
            return false;

        final JsonNode node = schema.get("enum");

        if (node != null)
            values.addAll(CollectionUtils.toSet(node.iterator()));
        else
            voidEnum = true;

        return true;
    }

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        if (voidEnum)
            return true;
        if (values.contains(node))
            return true;

        messages.add("node does not match any value in the enumeration");
        return false;
    }
}
