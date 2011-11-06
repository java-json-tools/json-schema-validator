/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.syntax;

import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.util.NodeType;

/**
 * Specialized syntax validator for integer values. This is needed for two
 * reasons:
 * <ul>
 *     <li>all keywords implementing this abstract class have positive
 *     integer values only;</li>
 *     <li>all Java methods used for these validators return an integer,
 *     which maximum, {@link Integer#MAX_VALUE}, is 2^31 - 1.</li>
 * </ul>
 */
public abstract class PositiveIntegerSyntaxValidator
    extends AbstractSyntaxValidator
{
    protected PositiveIntegerSyntaxValidator(final ValidationContext context,
        final String keyword)
    {
        super(context, keyword, NodeType.INTEGER);
    }

    @Override
    protected final void checkFurther()
    {
        if (!node.isInt()) {
            report.addMessage("value is too large");
            return;
        }

        if (node.getIntValue() < 0)
            report.addMessage("value is negative");
    }
}
