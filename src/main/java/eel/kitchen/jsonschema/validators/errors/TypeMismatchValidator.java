package eel.kitchen.jsonschema.validators.errors;

import eel.kitchen.jsonschema.validators.Validator;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.List;

public final class TypeMismatchValidator
    implements Validator
{
    private final String message;

    public TypeMismatchValidator(final List<String> types, final String actual)
    {
        switch (types.size()) {
        case 0:
            message = "schema does not allow any type??";
            break;
        case 1:
            message = String.format("node is of type %s, expected %s",
                actual, types.get(0));
            break;
        default:
            message = String.format("node is of type %s, "
                + "expected one of %s", actual, types);
        }
    }

    @Override
    public void setup()
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
        return Arrays.asList(message);
    }

    @Override
    public JsonNode getSchemaForPath(final String subPath)
    {
        return null;
    }
}
