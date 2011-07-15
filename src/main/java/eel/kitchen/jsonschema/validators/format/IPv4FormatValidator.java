package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public final class IPv4FormatValidator
    extends AbstractValidator
{
    @Override
    protected boolean doValidate(final JsonNode node)
    {
        try {
            Inet4Address.getByName(node.getTextValue());
            return true;
        } catch (UnknownHostException e) {
            messages.add("string is not a valid IPv4 address");
            return false;
        }
    }
}
