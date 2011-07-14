package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CSSStyleValidator
    extends AbstractValidator
{
    private static final Pattern pattern
        = Pattern.compile("^\\s*[^:]+\\s*:\\s*[^;]+$", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean validate(final JsonNode node)
    {
        final String[] rules = node.getTextValue().split("\\s*;\\s*");
        Matcher matcher;

        messages.clear();

        for (final String rule: rules) {
            matcher = pattern.matcher(rule);
            if (!matcher.lookingAt()) {
                messages.add("string is not a valid CSS 2.1 style");
                return false;
            }
        }
        return true;
    }
}
