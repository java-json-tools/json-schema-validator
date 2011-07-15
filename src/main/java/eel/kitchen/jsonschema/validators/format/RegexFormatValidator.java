package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

public final class RegexFormatValidator
    extends AbstractValidator
{
    @Override
    protected boolean doValidate(final JsonNode node)
    {
        if (RhinoHelper.regexIsValid(node.getTextValue()))
            return true;

        messages.add("input is not a valid regular expression");
        return false;
    }
}
