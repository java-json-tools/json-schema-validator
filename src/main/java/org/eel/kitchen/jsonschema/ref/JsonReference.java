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
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.util.LRUMap;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.uri.URIHandlerFactory;
import org.eel.kitchen.util.JsonPointer;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * JSON Reference resolution
 *
 * <p>This class has only one purpose, but a very important one: resolve
 * references to what they eventually point to. It also detects reference
 * loops.</p>
 *
 * <p>Note that there is no guarantee that the resolved JSON instance is a
 * JSON schema at all, or even an object for that matter.</p>
 */
public final class JsonReference
{
    /**
     * Cache of already downloaded absolute references
     */
    private static final Map<JsonRef, JsonNode> cache
        = new LRUMap<JsonRef, JsonNode>(10, 50);

    /**
     * URI resolver
     */
    private static final URIHandlerFactory factory = new URIHandlerFactory();

    /**
     * Try and lookup a JSON object with a given {@code id} member
     *
     * <p>Note that this is quite buggy for the time being,
     * it will return any object it can find. Trying and looking up {@code #foo}
     * in this schema, for instance:</p>
     *
     * <pre>
     *     {
     *         "enum": [ { "id": "#foo", "hello": true } ]
     *     }
     * </pre>
     *
     * <p>will return the value within the enum. It shouldn't.</p>
     *
     * @param dst the node to lookup
     * @param fragment the fragment to lookup
     * @return the found document, or a {@link MissingNode} if none is found
     */
    private static JsonNode getByID(final JsonNode dst, final String fragment)
    {
        /*
         * FIXME: this is a hack
         *
         * JsonSchema objects are not built with ID lookup support,
         * and not in depth.
         *
         * As a result, an invalid "id" field can pop up here,
         * and we have to deal with that.
         */

        if (!dst.isObject())
            return MissingNode.getInstance();

        if (dst.has("id")) {
            final JsonNode id = dst.get("id");
            if (!id.isTextual())
                return MissingNode.getInstance();
            if (id.textValue().replaceFirst("^#", "").equals(fragment))
                return dst;
        }

        // Cycle through children, we don't care about property names
        JsonNode tmp;

        for (final JsonNode child: dst) {
            tmp = getByID(child, fragment);
            if (!tmp.isMissingNode())
                return tmp;
        }

        return MissingNode.getInstance();
    }

    /**
     * Record a schema into {@link #cache} if and only if it has an {@code id}
     * member, and this ID is an <i>absolute</i> URI with no fragment
     *
     * <p>Even though the JSON Schema spec doesn't mandate it,
     * having a schema with an ID which is not an absolute URI is nonsense,
     * we therefore forbid it.</p>
     *
     * @param schema the JSON Schema
     * @return the reference for that schema, if any
     * @throws JsonSchemaException the ID is incorrect
     */
    private static JsonRef recordSchema(final JsonNode schema)
        throws JsonSchemaException
    {
        final JsonRef ret = JsonRef.fromNode(schema, "id");

        if (ret.hasFragment())
            throw new JsonSchemaException("a schema locator cannot have a non"
                + " empty fragment part");

        if (ret.isEmpty())
            return ret;

        if (!ret.isAbsolute())
            throw new JsonSchemaException("a schema locator must be absolute");

        synchronized (cache) {
            cache.put(ret, schema);
        }

        return ret;
    }

    /**
     * Resolve a fragment in a document, either a JSON Pointer or an id
     *
     * @param schema the schema
     * @param fragment the fragment
     * @return the matching node (a {@link MissingNode} if not found)
     */
    private static JsonNode localResolve(final JsonNode schema,
        final String fragment)
    {
        try {
            return new JsonPointer(fragment).getPath(schema);
        } catch (JsonSchemaException ignored) {
            return getByID(schema, fragment);
        }
    }

    /**
     * Download a schema from a given JSON reference
     *
     * <p>This method is only called if the reference is absolute.</p>
     *
     * @param ref the JSON reference
     * @return the schema pointed to by that reference
     * @throws IOException downloading failed (network problems etc)
     */
    private static JsonNode resolveLocation(final JsonRef ref)
        throws IOException
    {
        JsonNode ret;

        synchronized (cache) {
            ret = cache.get(ref);

            if (ret != null)
                return ret;

            final URI locator = ref.getLocator();

            ret = factory.getDocument(locator);

            cache.put(ref, ret);
        }

        return ret;
    }

    /**
     * Resolve a reference relatively to a schema
     *
     * <p>This method is the core operating routine of this class,
     * and the only public member. It will resolve references recursively,
     * record schemas along its way and return the result. It will also
     * detect a loop.</p>
     *
     * @param schema the schema
     * @param node the node
     * @return the JSON instance
     * @throws JsonSchemaException reference loop detected, or malformed ref
     * @throws IOException downloading of a reference failed
     */
    public static JsonNode resolveRef(final JsonNode schema,
        final JsonNode node)
        throws JsonSchemaException, IOException
    {
        JsonRef locator, ref;
        JsonNode origin = schema, ret = node;
        final Set<JsonRef> reflist = new LinkedHashSet<JsonRef>();

        while (ret.has("$ref")) {
            locator = recordSchema(origin);
            ref = JsonRef.fromNode(node, "$ref");
            ref = locator.resolve(ref);
            if (!reflist.add(ref))
                throw new JsonSchemaException("ref \"" + ref
                    + "\" loops on itself");
            if (ref.isAbsolute())
                origin = resolveLocation(ref);
            ret = localResolve(origin, ref.getFragment());
        }

        return ret;
    }
}
