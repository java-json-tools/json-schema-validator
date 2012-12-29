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
import org.eel.kitchen.jsonschema.format.FormatAttribute;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.JsonLoader;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public enum BuiltinSchemas
{
    /**
     * Draft v3 core schema
     */
    DRAFTV3_CORE("http://json-schema.org/draft-03/schema#", "/draftv3/schema",
        "draft v3 core schema", KeywordRegistries.draftV3Core()),
    /**
     * Draft v4 core schema
     */
    DRAFTV4_CORE("http://json-schema.org/draft-04/schema#", "/draftv4/schema",
        "draft v4 core schema", KeywordRegistries.draftV4Core()),
    /**
     * Draft v3 LDO (Link Description Object) schema
     */
    DRAFTV3_LINKS("http://json-schema.org/draft-03/links#", "/draftv3/links",
        "draft v3 link description object (LDO) schema",
        KeywordRegistries.draftV3HyperSchema()),
    /**
     * Draft v3 hyper-schema
     */
    DRAFTV3_HYPERSCHEMA("http://json-schema.org/draft-03/hyper-schema#",
        "/draftv3/hyper-schema", "draft v3 hyper schema",
        KeywordRegistries.draftV3HyperSchema());

    private final JsonRef locator;
    private final JsonNode rawSchema;
    private final String desc;
    final Map<String, SyntaxChecker> checkers;
    final Map<String, Class<? extends KeywordValidator>> validators;
    final Map<String, FormatAttribute> formatAttributes;

    BuiltinSchemas(final String uri, final String resource, final String desc,
        final KeywordRegistry registry)
    {
        try {
            locator = JsonRef.fromString(uri);
            rawSchema = JsonLoader.fromResource(resource);
        } catch (JsonSchemaException e) {
            throw new ExceptionInInitializerError(e);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        this.desc = desc;
        // All .get() methods return immutable map instances, but that is
        // no less ugly
        checkers = registry.getSyntaxCheckers();
        validators = registry.getValidators();
        formatAttributes = registry.getFormatAttributes();
    }

    /**
     * Get the locator for this schema
     *
     * @return the locator as a URI
     */
    public URI getURI()
    {
        return locator.toURI();
    }

    /**
     * Get the raw JSON document for that schema
     *
     * @return the schema as a {@link JsonNode}
     */
    public JsonNode getRawSchema()
    {
        return rawSchema;
    }

    @Override
    public String toString()
    {
        return desc + " (" + locator + ')';
    }
}
