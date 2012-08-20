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

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public final class SchemaContainer
{
    private static final Logger logger
        = LoggerFactory.getLogger(SchemaContainer.class);

    private static final URI EMPTY_LOCATOR = URI.create("#");

    private final JsonNode schema;
    private final JsonRef locator;

    /**
     * Return a new container based on a schema
     *
     * <p>Note that if the {@code id} node exists and is an URI,
     * but not absolute, an {@link #anonymousSchema(JsonNode)} will be
     * returned.</p>
     *
     * @param schema the schema
     */
    public SchemaContainer(final JsonNode schema)
    {
        final JsonNode idNode = schema.path("id");
        JsonRef ref = JsonRef.emptyRef();

        if (JacksonUtils.nodeIsURI(idNode))
            try {
                ref = JsonRef.fromString(idNode.textValue());
            } catch (JsonSchemaException e) { // cannot happen
                throw new RuntimeException("WTF??", e);
            }

        if (!ref.isAbsolute()) {
            logger.warn("schema locator (" + ref + ") is not absolute! " +
                "Returning an anonymous schema");
            ref = JsonRef.emptyRef();
        }

        locator = ref;
        this.schema = cleanup(schema);
    }

    /**
     * Return a new container based on an URI and a schema
     *
     * <p>Note that if the provided URI is not absolute, an
     * {@link #anonymousSchema(JsonNode)} is returned.</p>
     *
     * @param uri the URI
     * @param node the schema
     */
    public SchemaContainer(final URI uri, final JsonNode node)
    {
        JsonRef ref = JsonRef.fromURI(uri);

        if (!ref.isAbsolute()) {
            logger.warn("schema locator (" + ref + ") is not absolute! " +
                "Returning an anonymous schema");
            ref = JsonRef.emptyRef();
        }

        locator = ref;
        schema = cleanup(node);
    }

    public static SchemaContainer anonymousSchema(final JsonNode node)
    {
        return new SchemaContainer(EMPTY_LOCATOR, node);
    }

    public JsonRef getLocator()
    {
        return locator;
    }

    public JsonNode getSchema()
    {
        return schema;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (getClass() != o.getClass())
            return false;

        final SchemaContainer that = (SchemaContainer) o;

        return locator.equals(that.locator)
            && schema.equals(that.schema);
    }

    @Override
    public int hashCode()
    {
        return 31 * locator.hashCode() + schema.hashCode();
    }

    private static JsonNode cleanup(final JsonNode schema)
    {
        if (!schema.has("id"))
            return schema;

        final ObjectNode ret = schema.deepCopy();

        ret.remove("id");
        return ret;
    }
}
