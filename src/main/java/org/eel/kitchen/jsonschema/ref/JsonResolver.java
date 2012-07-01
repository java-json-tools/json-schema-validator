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
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.SchemaRegistry;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

public final class JsonResolver
{
    private final SchemaRegistry registry;

    public JsonResolver(final SchemaRegistry registry)
    {
        this.registry = registry;
    }

    /*
     * It should be noted that $ref resolution will never "leak" schemas:
     * either a ref is a fragment, in which case it is resolved within the
     * container itself, or it is absolute, in which case the current
     * container is replaced with the newer container.
     *
     * If $ref is neither fragment-only or absolute,
     * then the original container MUST be addressable via an absolute URI as
      * well. If this is not the case, the SchemaContainer constructor will
      * fail.
      *
      * The above means we only have to collect the refs we go through in a
      * set: loop detection occurs if trying to add a ref in the set which
      * already exists.
      *
      * FIXME: a context should be the argument instead of a container
      */
    public SchemaNode resolve(final SchemaNode schemaNode)
        throws JsonSchemaException
    {
        final Set<JsonRef> refs = new LinkedHashSet<JsonRef>();
        SchemaContainer container = schemaNode.getContainer();
        JsonNode node;
        JsonRef ref;
        SchemaNode ret = schemaNode;

        while (true) {
            node = ret.getNode();
            if (!nodeIsRef(node))
                break;
            ref = JsonRef.fromNode(node, "$ref");
            ref = container.getLocator().resolve(ref);
            if (!refs.add(ref))
                throw new JsonSchemaException("ref loop detected");
            if (!container.contains(ref))
                container = registry.get(ref.getRootAsURI());
            ret = container.lookupFragment(ref.getFragment());
        }

        return ret;
    }

    private boolean nodeIsRef(final JsonNode node)
    {
        final JsonNode refNode = node.path("$ref");

        if (!refNode.isTextual())
            return false;

        try {
            new URI(refNode.textValue());
            return true;
        } catch (URISyntaxException ignored) {
            return false;
        }
    }
}
