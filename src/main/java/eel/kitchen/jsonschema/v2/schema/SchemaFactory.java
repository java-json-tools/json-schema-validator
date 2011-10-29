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

package eel.kitchen.jsonschema.v2.schema;

import eel.kitchen.jsonschema.v2.instance.Instance;
import eel.kitchen.jsonschema.v2.syntax.SchemaChecker;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.List;

public final class SchemaFactory
{
    private static final SchemaChecker checker = SchemaChecker.getInstance();
    private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    private final JsonNode rootSchema;

    public SchemaFactory(final JsonNode rootSchema)
    {
        this.rootSchema = rootSchema;
    }

    public static JsonNodeFactory getNodeFactory()
    {
        return nodeFactory;
    }

    public Schema getSchema(final JsonNode schema)
    {
        final List<String> messages = checker.check(this, schema);

        if (!messages.isEmpty())
            return failure(messages);

        //TODO: implement
        return null;
    }

    private static Schema failure(final List<String> messages)
    {
        return new Schema()
        {
            @Override
            public Schema getSchema(final String path)
            {
                return null;
            }

            @Override
            public void validate(final ValidationState state,
                final Instance instance)
            {
                state.addMessages(messages);
            }
        };

    }
}
