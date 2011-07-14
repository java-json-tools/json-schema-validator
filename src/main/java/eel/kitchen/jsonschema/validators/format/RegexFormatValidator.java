package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

public final class RegexFormatValidator
    extends AbstractValidator
{
    public RegexFormatValidator(final JsonNode ignored)
    {
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        messages.clear();

        if (RhinoHelper.regexIsValid(node.getTextValue()))
            return true;

        messages.add("input is not a valid regular expression");
        return false;
    }
}
