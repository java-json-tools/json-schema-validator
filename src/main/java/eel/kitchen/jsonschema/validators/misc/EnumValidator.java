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

    public EnumValidator()
    {
        registerField("enum", NodeType.ARRAY);
    }

    @Override
    protected boolean doSetup()
    {
        if (!super.doSetup())
            return false;

        values.addAll(CollectionUtils.toSet(schema.get("enum").iterator()));
        return true;
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
