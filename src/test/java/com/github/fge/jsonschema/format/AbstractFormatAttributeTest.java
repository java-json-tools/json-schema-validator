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

package com.github.fge.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.util.JsonLoader;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertNotNull;

public abstract class AbstractFormatAttributeTest
{
    protected final FormatAttribute attribute;
    protected final JsonNode testNode;
    protected final String fmt;

    protected AbstractFormatAttributeTest(
        final Dictionary<FormatAttribute> dict, final String prefix,
        final String fmt)
        throws IOException
    {
        final String resourceName = String.format("/format/%s/%s.json",
            prefix, fmt);
        this.fmt = fmt;
        testNode = JsonLoader.fromResource(resourceName);
        attribute = dict.get(fmt);
    }

    @Test
    public final void formatAttributeIsSupported()
    {
        assertNotNull(attribute, "no support for format attribute " + fmt);
    }
}
