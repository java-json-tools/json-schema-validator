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

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.util.NodeType;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public final class StringValidator
    extends AbstractValidator
{
    private static final Map<String, EnumSet<NodeType>> FIELDS
        = new LinkedHashMap<String, EnumSet<NodeType>>();

    private int minLength = 0, maxLength = Integer.MAX_VALUE;
    private String regex = null;

    public StringValidator()
    {
        registerField("minLength", NodeType.INTEGER);
        registerField("maxLength", NodeType.INTEGER);
        registerField("regex", NodeType.STRING);
    }

    @Override
    protected Map<String, EnumSet<NodeType>> fieldMap()
    {
        return FIELDS;
    }

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
        JsonNode node;

        if (schema.has("minLength")) {
            node = schema.get("minLength");
            if (!node.isInt())
                throw new MalformedJasonSchemaException("minLength should be " +
                    "an integer");
            minLength = node.getIntValue();
            if (minLength < 0)
                throw new MalformedJasonSchemaException("minLength should be " +
                    "greater than or equal to 0");
        }

        if (schema.has("maxLength")) {
            node = schema.get("maxLength");
            if (!node.isInt())
                throw new MalformedJasonSchemaException("maxLength should be " +
                    "an integer");
            maxLength = node.getIntValue();
            if (maxLength < 0)
                throw new MalformedJasonSchemaException("maxLength should be " +
                    "greater than or equal to 0");
        }

        if (maxLength < minLength)
            throw new MalformedJasonSchemaException("maxLength should be " +
                "greater than or equal to minLength");

        if (schema.has("pattern")) {
            node = schema.get("pattern");
            if (!node.isTextual())
                throw new MalformedJasonSchemaException("pattern should be a " +
                    "string");
            regex = node.getTextValue();
            if (!RhinoHelper.regexIsValid(regex))
                throw new MalformedJasonSchemaException("pattern is an " +
                    "invalid regular expression");
        }
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        final String value = node.getTextValue();
        final int len = value.length();

        messages.clear();

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
