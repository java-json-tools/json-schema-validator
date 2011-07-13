package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.type.NullValidator;
import org.codehaus.jackson.JsonNode;

public final class NullValidatorProvider
    extends AbstractValidatorProvider
{
    public NullValidatorProvider(final JsonNode schemaNode)
    {
        super(schemaNode, "null", NullValidator.class, false, false);
    }
}
