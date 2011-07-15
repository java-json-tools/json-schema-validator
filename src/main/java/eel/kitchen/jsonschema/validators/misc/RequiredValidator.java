package eel.kitchen.jsonschema.validators.misc;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;

import java.util.HashSet;
import java.util.Set;

public final class RequiredValidator
    extends AbstractValidator
{
    private final Set<String> required = new HashSet<String>();

    @Override
    protected boolean doSetup()
    {
        if (!super.doSetup())
            return false;

        required.clear();

        final JsonNode properties = schema.get("properties");
        if (properties == null)
            return true;

        final Set<String> fields
            = CollectionUtils.toSet(properties.getFieldNames());

        JsonNode node;

        for (final String field: fields) {
            node = properties.get(field).get("required");
            if (node == null)
                continue;
            if (!node.isBoolean()) {
                messages.add("required should be a boolean");
                return false;
            }
            if (node.getBooleanValue())
                required.add(field);
        }

        return true;
    }

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final Set<String> remaining = new HashSet<String>(required);

        remaining.removeAll(CollectionUtils.toSet(node.getFieldNames()));

        if (remaining.isEmpty())
            return true;

        for (final String field: remaining)
            messages.add(String
                .format("property %s is required but was not " + "found",
                    field));

        return false;
    }
}
