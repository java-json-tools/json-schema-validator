package eel.kitchen.jsonschema.validators.errors;

import eel.kitchen.jsonschema.validators.AbstractValidator;

import java.util.List;

/**
 * <p>Utility validator, always triggering a validation failure. This validator
 * will be spawned when:</p>
 *
 * <ul>
 *     <li>the given schema will not allow any type at all (for instance:
 *     <code>"disallow": "any"</code>, but also <code>"type":
 *     "integer"</code> combined with <code>"disallow": "number"</code></li>;
 *     <li>the given schema allows for one, or a set of, certain node types,
 *     but the node to be validated is neither of those.
 *     </li>
 * </ul>
 *
 */
public final class TypeMismatchValidator
    extends AbstractValidator
{
    /**
     * Constructor for this particular validator.
     *
     * @param types The list of allowed types for this validator, may be empty
     * @param actual The type of the node which is to be validated
     */
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
