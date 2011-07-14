package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public final class IPv4FormatValidator
    extends AbstractValidator
{
    public IPv4FormatValidator(final JsonNode ignored)
    {
    }

    public IPv4FormatValidator()
    {
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        messages.clear();

        try {
            Inet4Address.getByName(node.getTextValue());
            return true;
        } catch (UnknownHostException e) {
            messages.add("string is not a valid IPv4 address");
            return false;
        }
    }
}
