/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.validators.format;

import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>CSS 2.1 color validation. EXPERIMENTAL, I haven't even read the spec
 * yet.</p>
 *
 * <p>This is done using regex matching, and a fixed table containing the
 * 17 colors defined by CSS 2.1. The problem is I have not yet found an
 * external library able to validate all possible values,
 * and even this implementation is probably incomplete. The best candidate
 * would probably be jStyleParser from
 * <a href="http://cssbox.sf.net">cssbox</a>, but I haven't managed to use it
 * properly yet.</p>
 */
public final class CSSColorValidator
    extends AbstractFormatValidator
{
    /**
     * The 17 color names defined by CSS 2.1
     */
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

    /**
     * <p>Patterns to recognize hash-defined colors (#xxx...) and rgb-defined
     * colors (rgb(x,y,z)).</p>
     *
     * <p>As we use .matches(), we don't need to anchor regexes.</p>
     */
    private static final Pattern
        hash = Pattern.compile("#[\\da-f]{1,6}", Pattern.CASE_INSENSITIVE),
        rgb = Pattern.compile("rgb\\(([^)]+)\\)");

    /**
     * Validate this instance.
     *
     * @param node the instance to validate
     * @return true if the instance is valid
     */
    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final String value = node.getTextValue();

        if (colorNames.contains(value.toLowerCase()))
            return true;

        Matcher matcher;

        matcher = hash.matcher(value);

        if (matcher.matches())
            return true;

        matcher = rgb.matcher(value);

        if (!matcher.matches()) {
            messages.add("string is not a valid CSS 2.1 color");
            return false;
        }

        final String[] colors = matcher.group(1).split("\\s*,\\s*");

        if (colors.length != 3) {
            messages.add("string is not a valid CSS 2.1 color");
            return false;
        }

        int i;

        for (final String color: colors) {
            try {
                i = Integer.parseInt(color);
                /*
                 * A color element must not be negative or greater than 255.
                 * This means right shifting by 8 should yield 0.
                 */
                if ((i >> 8) != 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                messages.add("string is not a valid CSS 2.1 color");
                return false;
            }
        }

        return true;
    }
}
