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

package eel.kitchen.jsonschema.v2.keyword;

import eel.kitchen.jsonschema.v2.schema.MatchAnySchema;
import eel.kitchen.jsonschema.v2.schema.Schema;
import eel.kitchen.jsonschema.v2.schema.SingleSchema;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.LinkedHashSet;
import java.util.Set;

public final class TypeKeywordValidator
    extends TypesetKeywordValidator
{
    public TypeKeywordValidator(final JsonNode schema)
    {
        super("type", schema);
    }

    @Override
    public ValidationStatus validate(final JsonNode instance)
    {
        if (!typeSet.contains(NodeType.getNodeType(instance)))
            return ValidationStatus.FAILURE;

        if (nextSchemas.isEmpty())
            return ValidationStatus.SUCCESS;

        return null;
    }

    @Override
    protected void buildNext()
    {
        if (nextSchemas.size() == 1) {
            nextSchema = new SingleSchema(factory,
                nextSchemas.iterator().next());
            return;
        }

        final Set<Schema> set = new LinkedHashSet<Schema>();
        Schema schema;

        for (final JsonNode element: nextSchemas) {
            schema = new SingleSchema(factory, element);
            set.add(schema);
        }

        nextSchema = new MatchAnySchema(set);
    }
}
