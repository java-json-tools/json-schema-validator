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

package org.eel.kitchen.jsonschema.main;

import org.eel.kitchen.jsonschema.report.Domain;
import org.eel.kitchen.jsonschema.report.Message;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class JsonSchemaExceptionTest
{
    @Test
    public void nonJsonSchemaExceptionIsNotUnwrapped()
    {
        final Throwable t = new IOException("foo");
        final Message msg = sampleMessage();

        final JsonSchemaException e = JsonSchemaException.wrap(msg, t);

        assertSame(e.getCause(), t);
        assertSame(e.getValidationMessage(), msg);
    }

    @Test
    public void innerJsonSchemaExceptionIsUnwrapped()
    {

        final Throwable inner = sampleException();
        final JsonSchemaException e
            = JsonSchemaException.wrap(sampleMessage(), inner);

        assertSame(e, inner);
    }

    @Test
    public void unwrappingOnlyGoesAsFarAsJsonSchemaException()
    {
        final JsonSchemaException inner = sampleException();
        final Throwable t = new Exception(inner);
        final JsonSchemaException e
            = JsonSchemaException.wrap(sampleMessage(), t);

        assertSame(e.getCause(), t);
    }

    private static Message sampleMessage()
    {
        return Domain.SYNTAX.newMessage().setKeyword("foo").setMessage("foo")
            .build();
    }

    private static JsonSchemaException sampleException()
    {
        return new JsonSchemaException(sampleMessage());
    }
}
