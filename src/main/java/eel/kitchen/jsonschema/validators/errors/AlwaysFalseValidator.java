package eel.kitchen.jsonschema.validators.errors;

import eel.kitchen.jsonschema.validators.AbstractValidator;

import java.util.Collection;

public final class AlwaysFalseValidator
    extends AbstractValidator
{
    public AlwaysFalseValidator(final String message)
    {
        messages.add(message);
    }

    public AlwaysFalseValidator(final Collection<String> messages)
    {
        this.messages.addAll(messages);
    }
}
