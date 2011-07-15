package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HostnameFormatValidator
    extends AbstractValidator
{
    private static final Pattern hostnamePart
        = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$", Pattern.CASE_INSENSITIVE);

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final String value = node.getTextValue();
        final String[] parts = value.split("\\.");
        Matcher matcher;

        for (final String part: parts) {
            matcher = hostnamePart.matcher(part);
            if (!matcher.matches()) {
                messages.add("string is not a valid hostname");
                return false;
            }
        }

        return true;
    }
}
