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

import eel.kitchen.jsonschema.v2.schema.Schema;
import eel.kitchen.jsonschema.v2.schema.SchemaFactory;
import eel.kitchen.jsonschema.v2.schema.ValidationMode;
import eel.kitchen.jsonschema.v2.schema.ValidationState;
import org.codehaus.jackson.JsonNode;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static eel.kitchen.jsonschema.v2.schema.ValidationMode.*;

public final class ExtendsKeywordValidator
    extends ExtensibleKeywordValidator
{
    public ExtendsKeywordValidator(final JsonNode schema)
    {
        super(schema);
    }

    @Override
    protected void buildNext(final SchemaFactory factory)
    {
        final JsonNode extendsNode = schema.get("extends");

        if (extendsNode.isObject()) {
            nextSchema = factory.buildSingleSchema(VALIDATE_NORMAL, extendsNode);
            return;
        }

        final EnumSet<ValidationMode> mode
            = EnumSet.of(VALIDATE_NORMAL, VALIDATE_ALL);

        final Set<Schema> set = new HashSet<Schema>();

        for (final JsonNode element: extendsNode)
            set.add(factory.buildSingleSchema(VALIDATE_NORMAL, element));

        nextSchema = factory.buildSchemaFromSet(mode, set);
    }

    @Override
    public void validate(final ValidationState state, final JsonNode node)
    {
        buildNext(state.getFactory());
        state.setNextSchema(nextSchema);
    }
}
