package eel.kitchen.jsonschema.validators.format;

import org.codehaus.jackson.JsonNode;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class IPv4FormatValidator
    extends AbstractFormatValidator
{
    @Override
    public boolean validate(final JsonNode node)
    {
        validationErrors.clear();

        try {
            Inet4Address.getByName(node.getTextValue());
            return true;
        } catch (UnknownHostException e) {
            validationErrors.add("string is not a valid IPv4 address");
            return false;
        }
    }
}
