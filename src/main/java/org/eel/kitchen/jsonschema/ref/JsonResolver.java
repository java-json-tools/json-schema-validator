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
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;
import org.eel.kitchen.jsonschema.uri.URIManager;

import java.util.LinkedHashSet;
import java.util.Set;

public final class JsonResolver
{
    private final URIManager manager;

    public JsonResolver(final URIManager manager)
    {
        this.manager = manager;
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
        JsonNode node = schemaNode.getNode();
        JsonRef ref;

        while (node.has("$ref")) {
            try {
                ref = JsonRef.fromNode(node, "$ref");
            } catch (JsonSchemaException ignored) {
                // Let syntax validation handle this case
                return new SchemaNode(container, node);
            }
            ref = container.getLocator().resolve(ref);
            if (!refs.add(ref))
                throw new JsonSchemaException("ref loop detected");
            if (!container.contains(ref)) {
                node = manager.getContent(ref.getLocator());
                container = new SchemaContainer(node);
            }
            node = container.lookupFragment(ref.getFragment());
        }

        return new SchemaNode(container, node);
    }
}
