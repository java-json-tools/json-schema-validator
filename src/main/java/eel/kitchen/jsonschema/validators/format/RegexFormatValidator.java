package eel.kitchen.jsonschema.validators.format;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Perl5Compiler;
import org.codehaus.jackson.JsonNode;

public class RegexFormatValidator
    extends AbstractFormatValidator
{
    @Override
    public boolean validate(final JsonNode node)
    {
        validationErrors.clear();

        try {
            new Perl5Compiler().compile(node.getTextValue());
            return true;
        } catch (MalformedPatternException e) {
            validationErrors.add("input is not a valid regular expression");
            return false;
        }
    }
}
