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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.eel.kitchen.jsonschema.bundle.Keyword;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.bundle.KeywordBundles;
import org.eel.kitchen.jsonschema.keyword.KeywordFactory;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.ref.SchemaRegistry;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.uri.URIDownloader;
import org.eel.kitchen.jsonschema.uri.URIManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class JsonSchemaFactory
{
    private final Set<JsonNode> validated = new HashSet<JsonNode>();
    private final LoadingCache<JsonNode, Set<KeywordValidator>> cache;

    private final SyntaxValidator syntaxValidator;
    private final KeywordFactory keywordFactory;
    private final SchemaRegistry registry;

    private JsonSchemaFactory(final Builder builder)
    {
        syntaxValidator = new SyntaxValidator(builder.bundle);
        keywordFactory = new KeywordFactory(builder.bundle);
        registry = new SchemaRegistry(builder.uriManager);

        final CacheLoader<JsonNode, Set<KeywordValidator>> loader
            = new CacheLoader<JsonNode, Set<KeywordValidator>>()
        {
            @Override
            public Set<KeywordValidator> load(final JsonNode key)
            {
                return keywordFactory.getValidators(key);
            }
        };

        cache = CacheBuilder.newBuilder().maximumSize(100L).build(loader);
    }

    public SchemaContainer registerSchema(final JsonNode schema)
        throws JsonSchemaException
    {
        return registry.register(schema);
    }

    public JsonSchema createSchema(final SchemaContainer container)
    {
        return createSchema(container, container.getSchema());
    }

    public JsonSchema createSchema(final SchemaContainer container,
        final JsonNode schema)
    {
        return new JsonSchema(this, new SchemaNode(container, schema));
    }

    public void validateSyntax(final List<String> messages, final JsonNode node)
    {
        synchronized (validated) {
            if (validated.contains(node))
                return;
            syntaxValidator.validate(messages, node);
            if (messages.isEmpty())
                validated.add(node);
        }
    }

    public Set<KeywordValidator> getValidators(final JsonNode node)
    {
        return cache.getUnchecked(node);
    }

    public SchemaRegistry getRegistry()
    {
        return registry;
    }

    public static final class Builder
    {
        private final KeywordBundle bundle;
        private final URIManager uriManager;

        public Builder()
        {
            this(KeywordBundles.defaultBundle());
        }

        public Builder(final KeywordBundle bundle)
        {
            this.bundle = bundle;
            uriManager = new URIManager();
        }
        public Builder addURIDownloader(final String scheme,
            final URIDownloader downloader)
        {
            uriManager.registerDownloader(scheme, downloader);
            return this;
        }

        public Builder addKeyword(final Keyword keyword)
        {
            bundle.registerKeyword(keyword);
            return this;
        }

        public JsonSchemaFactory build()
        {
            return new JsonSchemaFactory(this);
        }
    }
}
