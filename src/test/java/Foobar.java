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

import eel.kitchen.jsonschema.v2.instance.AtomicInstance;
import eel.kitchen.jsonschema.v2.instance.Instance;
import eel.kitchen.jsonschema.v2.schema.Schema;
import eel.kitchen.jsonschema.v2.schema.SchemaFactory;
import eel.kitchen.jsonschema.v2.schema.ValidationMode;
import eel.kitchen.jsonschema.v2.schema.ValidationState;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.Map;

public final class Foobar
{
    public static void main(final String... args)
        throws IOException
    {
        final JsonNode testNode = JasonHelper.load("/integer.json");

        final Map<String, JsonNode> map = CollectionUtils.toMap(testNode
            .getFields());

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            operateOne(entry.getKey(), entry.getValue());

        System.exit(0);
    }

    private static void operateOne(final String key, final JsonNode value)
    {
        System.out.println("*** " + key);

        final JsonNode schemaNode = value.get("schema");

        final SchemaFactory factory = new SchemaFactory(schemaNode);

        ValidationState state;

        state = new ValidationState(factory);

        JsonNode target = value.get("good");

        Schema schema = factory.buildSingleSchema(ValidationMode
            .VALIDATE_NORMAL, schemaNode);

        Instance instance = new AtomicInstance("", target);

        schema.validate(state, instance);

        System.out.println("Valid: " + !state.isFailure());

        for (final String message: state.getMessages())
            System.out.println(message);

        state = new ValidationState(factory);

        target = value.get("bad");

        instance = new AtomicInstance("", target);

        schema.validate(state, instance);

        System.out.println("Valid: " + !state.isFailure());

        for (final String message: state.getMessages())
            System.out.println(message);

    }
}
