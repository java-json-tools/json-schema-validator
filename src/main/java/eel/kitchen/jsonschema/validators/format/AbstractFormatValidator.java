package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.Validator;
import org.codehaus.jackson.JsonNode;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractFormatValidator
    implements Validator
{
    protected final List<String> validationErrors = new LinkedList<String>();

    protected AbstractFormatValidator(final JsonNode ignored)
    {
    }

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
    }

    @Override
    public List<String> getValidationErrors()
    {
        return validationErrors;
    }

    @Override
    public JsonNode getSchemaForPath(final String subPath)
    {
        return null;
    }
}
