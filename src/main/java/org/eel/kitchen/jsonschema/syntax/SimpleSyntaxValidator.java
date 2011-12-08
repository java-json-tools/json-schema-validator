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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.syntax;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

/**
 * Simple type-checking only validator
 *
 * <p>Most keywords have primitive types as their only possible types,
 * and don't need to check the value. This is therefore the class of choice
 * to inherit from in place of {@link SyntaxValidator}.
 * </p>
 *
 * @see SyntaxValidator
 * @see PositiveIntegerSyntaxValidator
 * @see URISyntaxValidator
 */
public abstract class SimpleSyntaxValidator
    extends SyntaxValidator
{
    /**
     * Constructor
     *
     * @param keyword the keyword to check
     * @param types the valid list of types for this keyword
     */
    protected SimpleSyntaxValidator(final String keyword, final NodeType... types)
    {
        super(keyword, types);
    }

    @Override
    protected final void checkFurther(final JsonNode schema,
        final ValidationReport report)
    {
    }
}
