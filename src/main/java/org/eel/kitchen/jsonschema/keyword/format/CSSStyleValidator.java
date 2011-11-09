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

package org.eel.kitchen.jsonschema.keyword.format;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.ValidationReport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attempt to recognize a CSS 2.1 style ("style" format specification in the
 * draft). Ideally, it should use something like jStyleParser.
 */
public final class CSSStyleValidator
    extends AbstractFormatValidator
{
    /**
     * <p>Pattern to recognize one style element. It is assumed that a style
     * element has the shape "something: whatever".</p>
     *
     * <p>This is used with {@link Matcher#matches()}, so the regex needs not
     * be anchored.</p>
     */
    private static final Pattern styleElement
        = Pattern.compile("\\s*[^:]+\\s*:\\s*[^;]+", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern used to split style elements
     */
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s*;\\s*");

    public CSSStyleValidator(final ValidationReport report, final JsonNode node)
    {
        super(report, node);
    }

    @Override
    public ValidationReport validate()
    {
        final String[] rules = SPLIT_PATTERN.split(node.getTextValue());
        Matcher matcher;

        for (final String rule : rules) {
            matcher = styleElement.matcher(rule);
            if (!matcher.matches()) {
                report.addMessage("string is not a valid CSS 2.1 style");
                break;
            }
        }
        return report;
    }

}
