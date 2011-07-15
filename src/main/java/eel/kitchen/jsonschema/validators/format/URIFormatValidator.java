package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.net.URI;
import java.net.URISyntaxException;

public final class URIFormatValidator
    extends AbstractValidator
{
    @Override
    protected boolean doValidate(final JsonNode node)
    {
        try {
            new URI(node.getTextValue());
            return true;
        } catch (URISyntaxException e) {
            messages.add("string is not a valid URI");
            return false;
        }
    }
}
