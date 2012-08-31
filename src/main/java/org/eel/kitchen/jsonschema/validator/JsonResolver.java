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

package org.eel.kitchen.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.ref.SchemaContainer;
import org.eel.kitchen.jsonschema.ref.SchemaNode;
import org.eel.kitchen.jsonschema.ref.SchemaRegistry;
import org.eel.kitchen.jsonschema.report.ValidationDomain;
import org.eel.kitchen.jsonschema.report.ValidationMessage;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class responsible for JSON Reference resolution
 *
 * <p>One instance of such class is created for each instance of {@link
 * JsonValidatorCache}.</p>
 */
final class JsonResolver
{
    private final SchemaRegistry registry;

    JsonResolver(final SchemaRegistry registry)
    {
        this.registry = registry;
    }

    /**
     * Resolve a schema node to the target schema node
     *
     * @see SchemaRegistry#get(URI)
     *
     * @param schemaNode the original schema node
     * @return the computed schema node
     * @throws JsonSchemaException ref resolution failure (dangling ref, ref
     * loop, etc)
     */
    SchemaNode resolve(final SchemaNode schemaNode)
        throws JsonSchemaException
    {
        final ValidationMessage.Builder msg
            = new ValidationMessage.Builder(ValidationDomain.REF_RESOLVING)
            .setKeyword("$ref");
        /*
         * These two elements might change during ref resolving. Set them to
         * their initial values.
         */
        SchemaContainer container = schemaNode.getContainer();
        JsonNode node = schemaNode.getNode();

        /*
         * This set will store all ABSOLUTE JSON references we encounter
         * during ref resolution. If there is an attempt to store an already
         * existing reference, this means a ref loop.
         *
         * We want to preserve insertion order, therefore we have to use a
         * LinkedHashSet.
         */
        final Set<JsonRef> refs = new LinkedHashSet<JsonRef>();

        /*
         * All elements below are set during the ref resolution process.
         */
        JsonRef source, ref, target;
        JsonNode refNode;

        while (true) {
            /*
             * We break out immediately if either there is no $ref node,
             * or there exists one but it is not a text node (syntax validation
             * will catch that).
             */
            refNode = node.path("$ref");
            if (!refNode.isTextual())
                break;
            /*
             * Similarly, the constructor will fail at this point iif the text
             * value of the node is not an URI: break, we want this caught by
             * syntax validation.
             */
            try {
                ref = JsonRef.fromString(refNode.textValue());
            } catch (JsonSchemaException ignored) {
                break;
            }
            /*
             * Compute the target ref. Try and insert it into the set of refs
             * already seen: if it has been already seen, there is a ref loop.
             */
            source = container.getLocator();
            target = source.resolve(ref);
            if (!refs.add(target)) {
                msg.setMessage("ref loop detected").addInfo("path", refs);
                throw new JsonSchemaException(msg.build());
            }
            /*
             * Should we change schema context? We should if the source ref (the
             * one in the current container) does not contain the target ref. In
             * this case, get the new container from our schema registry. If we
             * cannot do that, this is an error condition, bail out.
             */
            if (!source.contains(target))
                container = registry.get(target.getRootAsURI());
            /*
             * Finally, compute the next node in the process. If it is missing,
             * we have a dangling JSON Pointer: this is an error condition.
             */
            node = target.getFragment().resolve(container.getSchema());
            if (node.isMissingNode()) {
                msg.setMessage("dangling JSON Reference")
                    .addInfo("ref", target);
                throw new JsonSchemaException(msg.build());
            }
        }

        return new SchemaNode(container, node);
    }
}
