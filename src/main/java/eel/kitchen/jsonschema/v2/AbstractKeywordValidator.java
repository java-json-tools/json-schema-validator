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

package eel.kitchen.jsonschema.v2;

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractKeywordValidator
    implements KeywordValidator
{
    protected final EnumSet<NodeType> nodeTypes = EnumSet.noneOf(NodeType.class);
    protected final List<String> messages = new LinkedList<String>();
    protected boolean valid = true;

    protected AbstractKeywordValidator(final NodeType... types)
    {
        nodeTypes.addAll(Arrays.asList(types));
    }

    protected abstract boolean doValidate(final JsonNode instance);

    @Override
    public final EnumSet<NodeType> getNodeTypes()
    {
        return EnumSet.copyOf(nodeTypes);
    }

    @Override
    public final List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public final boolean validate(final JsonNode instance)
    {
        return valid && doValidate(instance);
    }
}
