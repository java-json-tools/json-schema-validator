/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.bugs;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.JsonValidator;
import org.eel.kitchen.jsonschema.main.ValidationConfig;
import org.eel.kitchen.jsonschema.main.ValidationReport;

/**
 * <code>$ref</code> bug
 *
 * <p>Schema:</p>
 *
 * <pre>
 *     {
 *         "type": "integer",
 *         "sub": {
 *             "$ref": "#",
 *             "divisibleBy": 2
 *         }
 *     }
 * </pre>
 *
 * <p>Data:</p>
 *
 * <pre>
 *     2
 * </pre>
 *
 * <p>Validate twice with:</p>
 *
 * <pre>
 *     validator.validate("#/sub", data);
 * </pre>
 *
 * <p>Expected: validation success.</p>
 * <p>What happens instead: ref loop detected on second run</p>
 * <p>What goes wrong: ref lookups are not cleared before the second run.</p>
 */
final class Bug1
    extends Bug
{
    public static void main(final String... args)
        throws JsonValidationFailureException
    {
        final JsonNode schema;
        final JsonNode value;
        int retcode = 0;

        ObjectNode node = factory.objectNode();
        node.put("type", "integer");

        ObjectNode node2 = factory.objectNode();
        node2.put("$ref", "#");
        node2.put("divisibleBy", 2);

        node.put("sub", node2);

        schema = node;

        value = factory.numberNode(3);

        final ValidationConfig cfg = new ValidationConfig();
        final JsonValidator validator = new JsonValidator(cfg, schema);

        ValidationReport report = validator.validate("#/sub", value);

        if (report.isError()) {
            retcode = 1;
            for (final String msg : report.getMessages())
                System.out.println(msg);
        }

        report = validator.validate("#/sub", value);

        if (report.isError()) {
            retcode = 1;
            for (final String msg : report.getMessages())
                System.out.println(msg);
        }
        System.exit(retcode);
    }
}
