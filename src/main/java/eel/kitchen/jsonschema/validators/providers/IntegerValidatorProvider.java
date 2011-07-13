package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.EnumValidator;
import eel.kitchen.jsonschema.validators.format.FormatValidator;
import eel.kitchen.jsonschema.validators.type.IntegerValidator;
import org.codehaus.jackson.JsonNode;

public final class IntegerValidatorProvider
    extends AbstractValidatorProvider
{
    public IntegerValidatorProvider(final JsonNode schemaNode)
    {
        super(schemaNode, "integer", IntegerValidator.class, true, true);
    }
}
