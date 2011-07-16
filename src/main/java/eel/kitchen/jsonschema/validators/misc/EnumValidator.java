/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.validators.misc;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Collection;
import java.util.HashSet;

public final class EnumValidator
    extends AbstractValidator
{
    private final Collection<JsonNode> values = new HashSet<JsonNode>();
    private boolean voidEnum = false;

    public EnumValidator()
    {
        registerField("enum", NodeType.ARRAY);
    }

    @Override
    protected boolean doSetup()
    {
        if (!super.doSetup())
            return false;

        final JsonNode node = schema.get("enum");

        if (node != null)
            values.addAll(CollectionUtils.toSet(node.iterator()));
        else
            voidEnum = true;

        return true;
    }

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        if (voidEnum)
            return true;
        if (values.contains(node))
            return true;

        messages.add("node does not match any value in the enumeration");
        return false;
    }
}
