package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public final class EmailFormatValidator
    extends AbstractValidator
{
    @Override
    public boolean validate(final JsonNode node)
    {
        messages.clear();

        try {
            new InternetAddress(node.getTextValue());
            return true;
        } catch (AddressException e) {
            messages.add("string is not a valid email address");
            return false;
        }
    }
}
