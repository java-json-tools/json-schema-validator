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

import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class BrokenEnumSchemasTest
{

    @Test
    public void testIllegalEnum()
        throws IOException
    {
        final JsonNode schemas = JasonHelper.load("broken-enum-schemas.json");
        final Validator v = new EnumValidator()
            .setSchema(schemas.get("illegal-enum"));

        assertFalse(v.setup());

        final List<String> messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "enum is of type boolean, "
            + "expected [array]");
    }
}
