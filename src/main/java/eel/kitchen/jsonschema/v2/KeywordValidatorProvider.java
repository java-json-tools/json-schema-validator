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

package eel.kitchen.jsonschema.v2;

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class KeywordValidatorProvider
{
    private static final Map<String, KeywordValidator> validators
        = new HashMap<String, KeywordValidator>();

    public static Set<KeywordValidator> getValidators(final JsonNode schema,
        final NodeType type)
    {
        final Set<KeywordValidator> ret = new HashSet<KeywordValidator>();

        final Iterator<String> fields = schema.getFieldNames();

        KeywordValidator validator;

        while (fields.hasNext()) {
            validator = validators.get(fields.next());
            if (validator == null)
                continue;
            if (!validator.getNodeTypes().contains(type))
                continue;
            ret.add(validator);
        }

        return ret;
    }
}
