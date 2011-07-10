package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.Validator;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.List;

public final class IllegalSchemaValidator
    implements Validator
{
    private final Exception e;

    public IllegalSchemaValidator(final Exception e)
    {
        this.e = e;
    }

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        return false;
    }

    @Override
    public List<String> getValidationErrors()
    {
        return Arrays.asList(String.format("BROKEN SCHEMA: %s: %s",
            e.getClass().getSimpleName(), e.getMessage()));
    }

    @Override
    public JsonNode getSchemaForPath(final String subPath)
    {
        return null;
    }
}