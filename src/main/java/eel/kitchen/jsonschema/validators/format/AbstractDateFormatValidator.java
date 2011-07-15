package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public abstract class AbstractDateFormatValidator
    extends AbstractValidator
{
    private final SimpleDateFormat format;
    private final String errmsg;

    protected AbstractDateFormatValidator(final String fmt, final String desc)
    {
        format = new SimpleDateFormat(fmt);
        errmsg = String.format("value is not a valid %s", desc);
    }

    @Override
    protected final boolean doValidate(final JsonNode node)
    {
        try {
            format.parse(node.getTextValue());
            return true;
        } catch (ParseException e) {
            messages.add(errmsg);
            return false;
        }
    }
}
