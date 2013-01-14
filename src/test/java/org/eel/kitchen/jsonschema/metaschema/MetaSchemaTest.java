/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.metaschema;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class MetaSchemaTest
{
    @Test
    public void cannotCreateMetaSchemaWithNoURI()
    {
        try {
            MetaSchema.builder().build();
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "a schema URI must be provided");
        }
    }

    @Test
    public void cannotCreateMetaSchemaWithNonAbsoluteJSONReference()
    {
        try {
            MetaSchema.builder().withURI("foo").build();
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "provided schema URI is not an " +
                "absolute JSON Reference");
        }
    }
}
