package eel.kitchen.jsonschema.validators.format;

public final class TimeFormatValidator
    extends AbstractDateFormatValidator
{
    public TimeFormatValidator()
    {
        super("HH:mm:ss", "time");
    }
}
