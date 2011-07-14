package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CSSColorValidator
    extends AbstractValidator
{
    private static final List<String> colorNames = Arrays.asList(
        "maroon",
        "red",
        "orange",
        "yellow",
        "olive",
        "green",
        "purple",
        "fuschia",
        "lime",
        "teal",
        "aqua",
        "blue",
        "navy",
        "black",
        "gray",
        "silver",
        "white"
    );

    private static final Pattern
        hash = Pattern.compile("^#[\\da-f]{1,6}$", Pattern.CASE_INSENSITIVE),
        rgb = Pattern.compile("^rgb\\(([^)]+)\\)$");

    public CSSColorValidator()
    {
    }

    private static final int USHORT_MAX = (1 << 8) - 1;

    public CSSColorValidator(final JsonNode ignored)
    {
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        final String value = node.getTextValue();

        messages.clear();

        if (colorNames.contains(value.toLowerCase()))
            return true;

        Matcher matcher;

        matcher = hash.matcher(value);

        if (matcher.lookingAt())
            return true;

        matcher = rgb.matcher(value);

        if (!matcher.lookingAt()) {
            messages.add("string is not a valid CSS 2.1 color");
            return false;
        }

        final String[] colors = matcher.group(1).split("\\s*,\\s*");

        if (colors.length != 3) {
            messages.add("string is not a valid CSS 2.1 color");
            return false;
        }

        for (final String color: colors) {
            final int i;
            try {
                i = Integer.parseInt(color);
                if ((i & ~USHORT_MAX) != 0)
                    throw new NumberFormatException("overflow");
            } catch (NumberFormatException e) {
                messages.add("string is not a valid CSS 2.1 color");
                return false;
            }
        }

        return true;
    }
}
