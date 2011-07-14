package eel.kitchen.jsonschema.validators.format;

import org.codehaus.jackson.JsonNode;

public final class TimeFormatValidator
    extends AbstractDateFormatValidator
{
    public TimeFormatValidator(final JsonNode ignored)
    {
        super(ignored, "HH:mm:ss", "time");
    }

    public TimeFormatValidator()
    {
        super("HH:mm:ss", "time");
    }
}
