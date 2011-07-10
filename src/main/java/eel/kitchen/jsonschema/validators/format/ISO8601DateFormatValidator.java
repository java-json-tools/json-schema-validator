package eel.kitchen.jsonschema.validators.format;

public class ISO8601DateFormatValidator
    extends AbstractDateFormatValidator
{
    public ISO8601DateFormatValidator()
    {
        super("yyyy-MM-dd'T'HH:mm:ssz", "ISO 8601 date-time");
    }
}
