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
import eel.kitchen.util.RhinoHelper;

public final class PatternValidator
    extends SyntaxValidator
{
    public PatternValidator(final ValidationContext context)
    {
        super(context, "pattern", NodeType.STRING);
    }

    /**
     * Check that the value is a valid regex
     *
     * @see {@link RhinoHelper#regexIsValid(String)}
     */
    @Override
    protected void checkFurther()
    {
        final String pattern = node.getTextValue();

        if (!RhinoHelper.regexIsValid(pattern))
            report.addMessage("invalid regex " + pattern);
    }
}
