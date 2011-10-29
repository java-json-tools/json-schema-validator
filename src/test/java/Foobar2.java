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
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;

public final class Foobar2
{
    private static final SchemaFactory factory;
    private static final JsonNode testNode;
    private static final Schema schema;

    static {
        try {
            testNode = JasonHelper.load("/v2/test.json");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        final JsonNode schemaNode = testNode.get("schema");

        factory = new SchemaFactory(schemaNode);

        schema = factory.buildSingleSchema(ValidationMode.VALIDATE_NORMAL,
            schemaNode);
    }

    public static void main(final String... args)
    {
        operateOneSet("good");
        operateOneSet("bad");

        System.exit(0);
    }

    private static void operateOneSet(final String s)
    {
        System.out.println("*** " + s);

        for (final JsonNode element: testNode.get(s)) {
            System.out.println("Trying " + element);
            operateOne(element);
        }
    }

    private static void operateOne(final JsonNode element)
    {
        final ValidationState state = new ValidationState(factory);

        final Instance instance = new AtomicInstance(element);

        schema.validate(state, instance);

        System.out.println("Valid: " + !state.isFailure());

        for (final String msg: state.getMessages())
            System.out.println(msg);
    }
}
