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

package org.eel.kitchen.jsonschema.ref;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.util.NodeAndPath;
import org.eel.kitchen.jsonschema.validator.JsonValidatorCache;

/**
 * Representation of a schema node
 *
 * <p>A schema node is the actual schema (as a {@link JsonNode} and the schema
 * context (as a {@link SchemaContainer}).</p>
 *
 * <p>This class has a critical performance role, as it is used as keys to the
 * validator cache. It is therefore important that it have very efficient
 * implementations of {@link Object#equals(Object)} and {@link
 * Object#hashCode()}.</p>
 *
 * <p>This class is thread safe and immutable.</p>
 *
 * @see JsonValidatorCache
 */
public final class SchemaNode
{
    private final SchemaContainer container;
    private final JsonNode node;
    private final JsonPointer path;
    private final int hashCode;

    public SchemaNode(final SchemaContainer container, final JsonNode node)
    {
        this(container, JsonPointer.empty(), node);
    }

    public SchemaNode(final SchemaContainer container, final JsonPointer path,
        final JsonNode node)
    {
        this.container = container;
        this.path = path;
        this.node = node;
        hashCode = 31 * container.hashCode() + path.hashCode();
    }

    public SchemaNode(final SchemaContainer container,
        final NodeAndPath nodeAndPath)
    {
        this(container, nodeAndPath.getPath(), nodeAndPath.getNode());
    }

    public SchemaContainer getContainer()
    {
        return container;
    }

    public JsonNode getNode()
    {
        return node;
    }

    public JsonPointer getPath()
    {
        return path;
    }

    public NodeAndPath getNodeAndPath()
    {
        return new NodeAndPath(node, path);
    }

    @Override
    public String toString()
    {
        return "locator: " + container.getLocator() + "; path: " + path;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;

        if (getClass() != obj.getClass())
            return false;

        final SchemaNode other = (SchemaNode) obj;

        return container.equals(other.container)
            && path.equals(other.path);
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }
}
