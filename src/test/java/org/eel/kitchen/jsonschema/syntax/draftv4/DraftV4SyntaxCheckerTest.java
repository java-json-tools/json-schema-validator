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

package org.eel.kitchen.jsonschema.syntax.draftv4;

import org.eel.kitchen.jsonschema.metaschema.BuiltinSchemas;
import org.eel.kitchen.jsonschema.syntax.AbstractSyntaxCheckerTest;

import java.io.IOException;

public abstract class DraftV4SyntaxCheckerTest
    extends AbstractSyntaxCheckerTest
{
    protected DraftV4SyntaxCheckerTest(final String resourceName)
        throws IOException
    {
        super(resourceName, BuiltinSchemas.DRAFTV4_CORE);
    }
}
