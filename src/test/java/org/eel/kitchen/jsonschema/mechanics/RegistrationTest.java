/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.mechanics;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.JsonValidator;
import org.testng.annotations.Test;

public final class RegistrationTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test(
        expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "^keyword is null$"
    )
    public void testNullKeywordRegistration()
    {
        final JsonValidator validator = new JsonValidator(factory.objectNode());

        validator.registerValidator(null, null, null);
    }

    @Test(
        expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "^keyword already registered to "
            + "that SyntaxFactory"
    )
    public void testExistingKeywordRegistrationFailure()
    {
        final JsonValidator validator = new JsonValidator(factory.objectNode());

        validator.registerValidator("default", null, null);
    }
}
