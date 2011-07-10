package eel.kitchen.jsonschema.validators.format;

public class TimeFormatValidator
    extends AbstractDateFormatValidator
{
    public TimeFormatValidator()
    {
        super("HH:mm:ss", "time");
    }
}
