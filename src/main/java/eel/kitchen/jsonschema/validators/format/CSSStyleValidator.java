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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Validate a CSS style. As for {@link CSSColorValidator},
 * this is highly experimental and done use regex matching as long as I
 * cannot manage to use cssbox properly. See the documentation of
 * CSSColorValidator for more detail.</p>
 *
 * <p>In particular, this validator will <i>not</i> check whether the style
 * elements are actually correct, it will just check (I <i>think</i>) that it
 * is well formed.</p>
 */
public final class CSSStyleValidator
    extends AbstractFormatValidator
{
    /**
     * <p>Pattern to recognize one style element. It is assumed that a style
     * element has the shape "something: whatever".</p>
     *
     * <p>Notes about anchored regexes and Java's matching mistakes apply.
     * Call me stubborn.</p>
     */
    private static final Pattern styleElement
        = Pattern.compile("^\\s*[^:]+\\s*:\\s*[^;]+$", Pattern.CASE_INSENSITIVE);

    /**
     * Validate this instance. It does so by splitting the input against the
     * semicolon and checking that each input is valid against the
     * styleElement regex.
     *
     * @param node the instance to validate
     * @return true if the instance is valid
     */
    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final String[] rules = node.getTextValue().split("\\s*;\\s*");
        Matcher matcher;

        for (final String rule: rules) {
            matcher = styleElement.matcher(rule);
            if (!matcher.lookingAt()) {
                messages.add("string is not a valid CSS 2.1 style");
                return false;
            }
        }
        return true;
    }
}
