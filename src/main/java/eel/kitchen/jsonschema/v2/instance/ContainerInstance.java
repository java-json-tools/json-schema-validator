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

package eel.kitchen.jsonschema.v2.instance;

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Iterator;
import java.util.Set;

abstract class ContainerInstance
    implements Instance
{
    private final String pathElement;
    private final NodeType nodeType;

    protected final JsonNode node;
    protected Set<Instance> children;

    ContainerInstance(final String pathElement, final JsonNode node,
        final NodeType nodeType)
    {
        this.pathElement = pathElement;
        this.node = node;
        this.nodeType = nodeType;
    }

    protected abstract void buildChildren();

    @Override
    public final JsonNode getRawInstance()
    {
        return node;
    }

    @Override
    public final NodeType getType()
    {
        return nodeType;
    }

    @Override
    public final String getPathElement()
    {
        return pathElement;
    }

    @Override
    public final String getAbsolutePath()
    {
        //TODO: implement
        return null;
    }

    @Override
    public final Iterator<Instance> iterator()
    {
        buildChildren();
        return children.iterator();
    }
}
