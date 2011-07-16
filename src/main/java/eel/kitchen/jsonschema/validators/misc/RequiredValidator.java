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

package eel.kitchen.jsonschema.validators.misc;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;

import java.util.HashSet;
import java.util.Set;

public final class RequiredValidator
    extends AbstractValidator
{
    private final Set<String> required = new HashSet<String>();

    @Override
    protected boolean doSetup()
    {
        required.clear();

        final JsonNode properties = schema.get("properties");
        if (properties == null)
            return true;

        final Set<String> fields
            = CollectionUtils.toSet(properties.getFieldNames());

        JsonNode node;

        for (final String field: fields) {
            node = properties.get(field).get("required");
            if (node == null)
                continue;
            if (!node.isBoolean()) {
                messages.add("required should be a boolean");
                return false;
            }
            if (node.getBooleanValue())
                required.add(field);
        }

        return true;
    }

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final Set<String> remaining = new HashSet<String>(required);

        remaining.removeAll(CollectionUtils.toSet(node.getFieldNames()));

        if (remaining.isEmpty())
            return true;

        for (final String field: remaining)
            messages.add(String
                .format("property %s is required but was not " + "found",
                    field));

        return false;
    }
}
