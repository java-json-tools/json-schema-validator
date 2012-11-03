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

package org.eel.kitchen.jsonschema.metaschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonRef;

import java.util.Map;

public final class SchemaProvider
{
    private final MetaSchema defaultMetaSchema;

    private final Map<JsonRef, MetaSchema> metaSchemas = Maps.newHashMap();

    public SchemaProvider(final MetaSchema metaSchema)
    {
        Preconditions.checkNotNull(metaSchema, "meta schema cannot be null");
        defaultMetaSchema = metaSchema;
        metaSchemas.put(metaSchema.getSchemaURI(), metaSchema);
    }

    public void addMetaSchema(final MetaSchema metaSchema)
    {
        Preconditions.checkNotNull(metaSchema, "meta schema cannot be null");

        final JsonRef ref = metaSchema.getSchemaURI();
        Preconditions.checkArgument(!metaSchemas.containsKey(ref),
            "schema URI " + ref + " already registered");
        metaSchemas.put(ref, metaSchema);
    }

    public MetaSchema getMetaSchema(final JsonNode schema)
    {
        final JsonNode node = schema.path("$schema");

        if (!node.isTextual())
            return defaultMetaSchema;

        try {
            final JsonRef ref = JsonRef.fromString(node.textValue());
            return metaSchemas.containsKey(ref) ? metaSchemas.get(ref)
                : defaultMetaSchema;
        } catch (JsonSchemaException ignored) {
            return defaultMetaSchema;
        }
    }
}
