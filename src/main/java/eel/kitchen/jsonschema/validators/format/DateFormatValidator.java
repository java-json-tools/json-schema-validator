package eel.kitchen.jsonschema.validators.format;

import org.codehaus.jackson.JsonNode;

public final class DateFormatValidator
    extends AbstractDateFormatValidator
{
    public DateFormatValidator(final JsonNode ignored)
    {
        super(ignored, "yyyy-MM-dd", "date");
    }
}
