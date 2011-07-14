package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.net.Inet6Address;
import java.net.UnknownHostException;

public final class IPv6FormatValidator
    extends AbstractValidator
{
    public IPv6FormatValidator(final JsonNode ignored)
    {
    }

    public IPv6FormatValidator()
    {
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        messages.clear();

        try {
            Inet6Address.getByName(node.getTextValue());
            return true;
        } catch (UnknownHostException e) {
            messages.add("string is not a valid IPv6 address");
            return false;
        }
    }
}
