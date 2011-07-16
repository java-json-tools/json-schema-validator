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

package eel.kitchen.jsonschema.validators.type;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.jsonschema.validators.format.FormatValidator;
import eel.kitchen.jsonschema.validators.misc.EnumValidator;
import eel.kitchen.util.NodeType;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

public final class StringValidator
    extends AbstractValidator
{
    private int minLength = 0, maxLength = Integer.MAX_VALUE;
    private String regex = null;

    public StringValidator()
    {
        registerField("minLength", NodeType.INTEGER);
        registerField("maxLength", NodeType.INTEGER);
        registerField("pattern", NodeType.STRING);

        registerValidator(new EnumValidator());
        registerValidator(new FormatValidator());
    }

    @Override
    protected boolean doSetup()
    {
        if (!computeMinMax())
            return false;

        final JsonNode pattern = schema.get("pattern");

        if (pattern == null)
            return true;

        regex = pattern.getTextValue();

        if (RhinoHelper.regexIsValid(regex))
            return true;

        messages.add("pattern is an invalid regular expression");
        return false;
    }

    private boolean computeMinMax()
    {
        final JsonNode min = schema.get("minLength"),
            max = schema.get("maxLength");

        if (min != null) {
            if (!min.isInt()) {
                messages.add("minLength overflow");
                return false;
            }
            minLength = min.getIntValue();
            if (minLength < 0) {
                messages.add("minLength is lower than 0");
                return false;
            }
        }

        if (max != null) {
            if (!max.isInt()) {
                messages.add("maxLength overflow");
                return false;
            }
            maxLength = max.getIntValue();
            if (maxLength < 0) {
                messages.add("maxLength is lower than 0");
                return false;
            }
        }

        if (maxLength < minLength) {
            messages.add("minLength is greater than maxLength");
            return false;
        }
        return true;
    }

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final String value = node.getTextValue();
        final int len = value.length();

        if (len < minLength) {
            messages.add("string length is less than the required minimum");
            return false;
        }

        if (len > maxLength) {
            messages.add("string length exceeds the required maximum");
            return false;
        }

        if (regex == null)
            return true;

        if (RhinoHelper.regMatch(regex, value))
            return true;

        messages.add("string does not match regular expression");

        return false;
    }
}
