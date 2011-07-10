package eel.kitchen.jsonschema.validators.format;

import org.codehaus.jackson.JsonNode;

import java.net.URI;
import java.net.URISyntaxException;

public final class URIFormatValidator
    extends AbstractFormatValidator
{
    protected URIFormatValidator(final JsonNode ignored)
    {
        super(ignored);
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        validationErrors.clear();

        try {
            new URI(node.getTextValue());
            return true;
        } catch (URISyntaxException e) {
            validationErrors.add("string is not a valid URI");
            return false;
        }
    }
}
