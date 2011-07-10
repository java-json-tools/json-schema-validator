package eel.kitchen.jsonschema.validators.errors;

import eel.kitchen.jsonschema.validators.AbstractValidator;

import java.util.List;

public final class TypeMismatchValidator
    extends AbstractValidator
{
    public TypeMismatchValidator(final List<String> types, final String actual)
    {
        final String message;
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
        validationErrors.add(message);
    }
}
