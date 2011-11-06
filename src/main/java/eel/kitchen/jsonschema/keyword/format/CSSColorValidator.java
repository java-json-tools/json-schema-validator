/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.keyword.format;

import eel.kitchen.jsonschema.ValidationReport;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: use Pattern for split()
public final class CSSColorValidator
    extends AbstractFormatValidator
{
    /**
     * The 17 color names defined by CSS 2.1
     */
    private static final List<String> colorNames = Arrays.asList("maroon",
         "red", "orange", "yellow", "olive", "green", "purple", "fuschia",
         "lime", "teal", "aqua", "blue", "navy", "black", "gray", "silver",
         "white");

    /**
     * <p>Patterns to recognize hash-defined colors (#xxx...) and rgb-defined
     * colors (rgb(x,y,z)).</p>
     *
     * <p>As we use .matches(), we don't need to anchor regexes.</p>
     */
    private static final Pattern
        hash = Pattern.compile("#[\\da-f]{1,6}", Pattern.CASE_INSENSITIVE),
        rgb = Pattern.compile("rgb\\(([^)]+)\\)");

    public CSSColorValidator(final ValidationReport report, final JsonNode node)
    {
        super(report, node);
    }

    @Override
    public ValidationReport validate()
    {
        doValidate();
        return report;
    }

    private void doValidate()
    {
        final String value = node.getTextValue();

        if (colorNames.contains(value.toLowerCase()))
            return;

        Matcher matcher;

        matcher = hash.matcher(value);

        if (matcher.matches())
            return;

        matcher = rgb.matcher(value);

        if (!matcher.matches()) {
            report.addMessage("string is not a valid CSS 2.1 color");
            return;
        }

        final String[] colors = matcher.group(1).split("\\s*,\\s*");

        if (colors.length != 3) {
            report.addMessage("string is not a valid CSS 2.1 color");
            return;
        }

        int i;

        for (final String color: colors) {
            try {
                i = Integer.parseInt(color);
                /*
                 * A color element must not be negative or greater than 255.
                 * This means right shifting by 8 should yield 0.
                 */
                if (i >> 8 != 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException ignored) {
                report.addMessage("string is not a valid CSS 2.1 color");
                return;
            }
        }
    }
}
