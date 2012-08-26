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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.keyword.KeywordFactory;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.SchemaRegistry;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class JsonValidationContext
{
    /**
     * Set of already validated schemas
     *
     * <p>FIXME: unbounded</p>
     */
    private final Set<JsonNode> validated = new HashSet<JsonNode>();

    /**
     * Cache for keyword validators
     */
    private final LoadingCache<JsonNode, Set<KeywordValidator>> cache;

    private final SyntaxValidator syntaxValidator;
    private final JsonResolverCache resolverCache;

    public JsonValidationContext(final KeywordBundle bundle,
        final SchemaRegistry registry)
    {
        resolverCache = new JsonResolverCache(registry);
        syntaxValidator = new SyntaxValidator(bundle);

        final CacheLoader<JsonNode, Set<KeywordValidator>> cacheLoader
            = buildCacheLoader(new KeywordFactory(bundle));

        cache = CacheBuilder.newBuilder().maximumSize(100L).build(cacheLoader);
    }

    SchemaNode resolve(final SchemaNode schemaNode)
        throws JsonSchemaException
    {
        return resolverCache.get(schemaNode);
    }

    /**
     * Method called by validators to check a schema syntax
     *
     * <p>Note that it is also at this level that non schemas (ie, JSON
     * documents which are not objects) are detected.</p>
     *
     * @param messages list of messages to fill
     * @param node the schema to validate
     */
    void validateSyntax(final List<String> messages, final JsonNode node)
    {
        if (!node.isObject()) {
            messages.add("not a JSON Schema (not an object)");
            return;
        }

        synchronized (validated) {
            if (validated.contains(node))
                return;
            syntaxValidator.validate(messages, node);
            if (messages.isEmpty())
                validated.add(node);
        }
    }

    /**
     * Method called by validators to obtain a keyword validator set for a
     * schema
     *
     * @param node the schema
     * @return a set of {@link KeywordValidator}
     */
    Set<KeywordValidator> getValidators(final JsonNode node)
    {
        return cache.getUnchecked(node);
    }

    private static CacheLoader<JsonNode, Set<KeywordValidator>>
        buildCacheLoader(final KeywordFactory keywordFactory)
    {
        return new CacheLoader<JsonNode, Set<KeywordValidator>>()
        {
            @Override
            public Set<KeywordValidator> load(final JsonNode key)
            {
                return keywordFactory.getValidators(key);
            }
        };
    }
}
