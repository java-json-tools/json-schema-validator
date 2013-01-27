/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.processing.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.processing.JsonSchemaContext;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ProcessingMessage;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.google.common.collect.Sets;

import java.util.Set;

public final class RefResolverProcessor
    implements Processor<RefResolverResult, JsonSchemaContext>
{
    private final SchemaLoader loader;

    public RefResolverProcessor(final SchemaLoader loader)
    {
        this.loader = loader;
    }

    @Override
    public RefResolverResult process(final JsonSchemaContext context)
        throws ProcessingException
    {
        final JsonSchemaTree orig = context.getSchemaTree();
        final JsonPointer origPtr = orig.getCurrentPointer();
        final Set<JsonRef> refs = Sets.newLinkedHashSet();
        final ProcessingMessage msg = context.newMessage();

        JsonSchemaTree current = orig.copy();
        boolean pointerChanged = false;

        JsonPointer ptr;
        JsonRef ref;
        JsonNode node;

        while(true) {
            /*
             * See if the current node is a JSON Reference
             */
            node = current.getCurrentNode();
            /*
             * If it isn't, we are done
             */
            ref = nodeAsRef(node);
            if (ref == null)
                break; // Done
            /*
             * Resolve the reference against the current context
             */
            ref = current.resolve(ref);
            /*
             * If we have seen this ref already, this is a ref loop
             */
            if (!refs.add(ref))
                throw new ProcessingException(msg
                    .msg("JSON reference loop detected")
                    .put("ref", ref).put("path", refs));
            /*
             * Check if that ref is resolvable within the current tree. If not,
             * fetch the new tree.
             *
             * This may fail, in which case we exit here.
             */
            if (!current.containsRef(ref))
                current = loader.get(ref.getLocator());
            /*
             * Get the appropriate pointer into the tree. If none, this means
             * a dangling reference
             */
            ptr = current.matchingPointer(ref);
            if (ptr == null)
                throw new ProcessingException(msg
                    .msg("unresolvable JSON reference")
                    .put("ref", ref).put("path", refs));
            current.setPointer(ptr);
            if (!(pointerChanged || ptr.equals(origPtr)))
                pointerChanged = true;
        }

        if (current.equals(orig)) {
            orig.setPointer(current.getCurrentPointer());
            current = orig;
        }
        return new RefResolverResult(current, current.equals(orig),
            pointerChanged);
    }

    private static JsonRef nodeAsRef(final JsonNode node)
    {
        final JsonNode refNode = node.path("$ref");
        if (!refNode.isTextual())
            return null;
        try {
            return JsonRef.fromString(refNode.textValue());
        } catch (JsonSchemaException ignored) {
            return null;
        }
    }
}
