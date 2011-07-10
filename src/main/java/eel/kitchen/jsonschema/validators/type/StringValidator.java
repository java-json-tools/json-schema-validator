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
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.codehaus.jackson.JsonNode;

public final class StringValidator
    extends AbstractValidator
{
    private int minLength = 0, maxLength = Integer.MAX_VALUE;

    private Pattern pattern = null;
    private final PatternMatcher matcher = new Perl5Matcher();

    public StringValidator(final JsonNode schemaNode)
    {
        super(schemaNode);
    }

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
        JsonNode node;

        if (schemaNode.has("minLength")) {
            node = schemaNode.get("minLength");
            if (!node.isInt())
                throw new MalformedJasonSchemaException("minLength should be " +
                    "an integer");
            minLength = node.getIntValue();
            if (minLength < 0)
                throw new MalformedJasonSchemaException("minLength should be " +
                    "greater than or equal to 0");
        }

        if (schemaNode.has("maxLength")) {
            node = schemaNode.get("maxLength");
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

        if (schemaNode.has("pattern")) {
            node = schemaNode.get("pattern");
            if (!node.isTextual())
                throw new MalformedJasonSchemaException("pattern should be a " +
                    "string");
            final String regex = node.getTextValue();
            try {
                pattern = new Perl5Compiler().compile(regex);
            } catch (MalformedPatternException e) {
                throw new MalformedJasonSchemaException("pattern is an " +
                    "invalid regular expression", e);
            }
        }
    }

    //TODO: format
    @Override
    public boolean validate(final JsonNode node)
    {
        final String value = node.getTextValue();
        final int len = value.length();

        if (len < minLength) {
            validationErrors.add("string length is less than the required minimum");
            return false;
        }

        if (len > maxLength) {
            validationErrors.add("string length exceeds the required maximum");
            return false;
        }

        if (pattern == null)
            return true;

        if (matcher.contains(value, pattern))
            return true;

        validationErrors.add("string does not match regular expression");

        return false;
    }
}
