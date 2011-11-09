package org.eel.kitchen.jsonschema.draftv4;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.jsonschema.keyword.SimpleKeywordValidator;
import org.eel.kitchen.util.CollectionUtils;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public final class RequiredKeywordValidator
    extends SimpleKeywordValidator
{
    /**
     * Constructor
     *
     * @param context  the context to use
     * @param instance the instance to validate
     */
    public RequiredKeywordValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);
    }

    @Override
    protected void validateInstance()
    {
        final SortedSet<String> required = new TreeSet<String>();

        for (final JsonNode element: schema.get("required"))
            required.add(element.getTextValue());

        final Set<String> instanceFields
            = CollectionUtils.toSet(instance.getFieldNames());

        required.removeAll(instanceFields);

        if (required.isEmpty())
            return;

        report.addMessage("missing dependencies " + required);
    }
}
