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

package org.eel.kitchen.jsonschema.syntax.common;

import org.eel.kitchen.jsonschema.syntax.URISyntaxValidator;

/**
 * Check for the <tt>$ref</tt> keyword
 *
 * <p>The only thing which should be checked here is that it is a valid URI
 * ... Although some URIs clearly don't make any sense at all.</p>
 *
 * <p>Unfortunately the spec authors are reluctant in recognizing this fact.
 * So, everything is allowed, and it is up to the implementation to handle
 * resolution, and yell if it cannot/does not want to handle an URI.
 * </p>
 */
public final class DollarRefSyntaxValidator
    extends URISyntaxValidator
{
    private static final DollarRefSyntaxValidator instance
        = new DollarRefSyntaxValidator();

    private DollarRefSyntaxValidator()
    {
        super("$ref");
    }

    public static DollarRefSyntaxValidator getInstance()
    {
        return instance;
    }
}
