package eel.kitchen.jsonschema.validators.format;

import org.codehaus.jackson.JsonNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public abstract class AbstractDateFormatValidator
    extends AbstractFormatValidator
{
    private final SimpleDateFormat format;
    private final String desc;

    protected AbstractDateFormatValidator(final String fmt, final String desc)
    {
        format = new SimpleDateFormat(fmt);
        this.desc = desc;
    }

    @Override
    public final boolean validate(final JsonNode node)
    {
        validationErrors.clear();

        try {
            format.parse(node.getTextValue());
            return true;
        } catch (ParseException e) {
            validationErrors.add(String.format("string is not a valid %s",
                desc));
            return false;
        }
    }
}
