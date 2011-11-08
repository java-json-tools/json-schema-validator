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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Specialized syntax validator used to validate URIs
 *
 * @see URI
 */
public abstract class URISyntaxValidator
    extends SyntaxValidator
{
    protected URISyntaxValidator(final ValidationContext context,
        final String keyword)
    {
        super(context, keyword, NodeType.STRING);
    }

    @Override
    protected final void checkFurther()
    {
        try {
            new URI(node.getTextValue());
        } catch (URISyntaxException ignored) {
            report.addMessage("not a valid URI");
        }
    }
}
