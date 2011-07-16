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

    private static final int USHORT_MAX = (1 << 8) - 1;
    private static final Pattern
        hash = Pattern.compile("^#[\\da-f]{1,6}$", Pattern.CASE_INSENSITIVE),
        rgb = Pattern.compile("^rgb\\(([^)]+)\\)$");

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final String value = node.getTextValue();

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
