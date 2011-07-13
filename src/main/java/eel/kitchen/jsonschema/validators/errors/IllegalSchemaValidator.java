package eel.kitchen.jsonschema.validators.errors;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.AbstractValidator;

/**
 * Utiliy validator to report schema validation errors. When a schema
 * validation error occurs which triggers an exception,
 * this validator will be invoked, will always FAIL to validate and report
 * the (simple) class name of the triggered exception and its message.
 *
 */

public final class IllegalSchemaValidator
    extends AbstractValidator
{
    /**
     * Constructor.
     *
     * @param e The {@link Exception} which has spawned this constructor in
     * the first place. In most cases, a {@link MalformedJasonSchemaException}.
     */

    public IllegalSchemaValidator(final Exception e)
    {
        messages.add(String.format("BROKEN SCHEMA: %s: %s",
            e.getClass().getSimpleName(), e.getMessage()));
    }
}