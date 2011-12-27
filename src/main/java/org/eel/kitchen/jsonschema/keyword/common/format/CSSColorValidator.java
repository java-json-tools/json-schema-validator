/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword.common.format;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attempt at validating a CSS 2.1 color ({@code color} format specification in
 * the draft). It is far from being perfect... Ideally, it should use something
 * like jStyleParser.
 */
public final class CSSColorValidator
    extends FormatValidator
{
    /**
     * The 17 color names defined by CSS 2.1
     */
    private static final List<String> colorNames = Arrays.asList("maroon",
         "red", "orange", "yellow", "olive", "green", "purple", "fuchsia",
         "lime", "teal", "aqua", "blue", "navy", "black", "gray", "silver",
         "white");

    /**
     * Pattern to recognize a "hash-defined" color
     */
    private static final Pattern
        hash = Pattern.compile("#([\\da-f][\\da-f][\\da-f]){1,2}",
            Pattern.CASE_INSENSITIVE);

    /**
     * Pattern to recognize an "rgb-defined" color
     */
    private static final Pattern rgb = Pattern.compile("rgb\\(([^)]+)\\)");

    /**
     * Pattern to split color elements in an "rgb-defined" color
     */
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s*,\\s*");

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        //TODO: more tests!
        final ValidationReport report = context.createReport();

        final String value = instance.getTextValue();

        if (colorNames.contains(value.toLowerCase()))
            return report;

        Matcher matcher;

        matcher = hash.matcher(value);

        if (matcher.matches())
            return report;

        matcher = rgb.matcher(value);

        if (!matcher.matches()) {
            report.fail("string is not a valid CSS 2.1 color");
            return report;
        }

        final String[] colors = SPLIT_PATTERN.split(matcher.group(1));

        if (colors.length != 3) {
            report.fail("string is not a valid CSS 2.1 color");
            return report;
        }

        final ColorType type = getElementType(colors[0]);

        if (type == ColorType.INVALID) {
            report.fail("string is not a valid CSS 2.1 color");
            return report;
        }

        if (type != getElementType(colors[1])) {
            report.fail("string is not a valid CSS 2.1 color");
            return report;
        }

        if (type != getElementType(colors[2])) {
            report.fail("string is not a valid CSS 2.1 color");
            return report;
        }

        return report;
    }

    private static ColorType getElementType(final String element)
    {
        ColorType ret = ColorType.INTEGER;
        final int i;
        String tmp = element;
        int max = 255;

        if (element.endsWith("%")) {
            tmp = element.substring(0, element.lastIndexOf("%"));
            max = 100;
            ret = ColorType.PERCENT;
        }

        try {
            i = Integer.parseInt(tmp);
        } catch (NumberFormatException ignored) {
            return ColorType.INVALID;
        }

        return i >= 0 && i <= max ? ret : ColorType.INVALID;
    }

    private enum ColorType
    {
        INTEGER,
        PERCENT,
        INVALID
    }
}
