package eel.kitchen.jsonschema.validators.errors;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class AlwaysFalseValidator
    extends AbstractValidator
{
    private final List<String> errors = new LinkedList<String>();

    public AlwaysFalseValidator(final String message)
    {
        errors.add(message);
    }

    public AlwaysFalseValidator(final Collection<String> messages)
    {
        errors.addAll(messages);
    }

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        messages.addAll(errors);
        return false;
    }

}
