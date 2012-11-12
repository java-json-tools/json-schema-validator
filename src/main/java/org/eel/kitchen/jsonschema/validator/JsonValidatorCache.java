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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.eel.kitchen.jsonschema.format.FormatAttribute;
import org.eel.kitchen.jsonschema.keyword.KeywordFactory;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.metaschema.KeywordRegistry;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.schema.SchemaNode;
import org.eel.kitchen.jsonschema.schema.SchemaRegistry;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.util.JacksonUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cache for JSON validators
 *
 * <p>This class plays a critically important role in the performance of the
 * whole validation process, since it is the class responsible for instantiating
 * validators and caching them for future reuse.</p>
 *
 * <p>As it uses a {@link LoadingCache}, it is totally thread safe and also
 * very efficient.</p>
 *
 * @see SchemaNode
 */
public final class JsonValidatorCache
{
    private static final JsonValidator ALWAYS_TRUE
        = new JsonValidator()
    {
        @Override
        public void validate(final ValidationContext context,
            final ValidationReport report, final JsonNode instance)
        {
        }
    };

    /**
     * Cache for all validators, even failing ones
     *
     * @see FailingValidator
     */
    private final LoadingCache<SchemaNode, JsonValidator> cache;

    private final JsonResolver resolver;
    private final SyntaxValidator syntaxValidator;
    private final KeywordFactory keywordFactory;

    private final Map<String, FormatAttribute> formatAttributes;

    public JsonValidatorCache(final KeywordRegistry keywordRegistry,
        final SchemaRegistry schemaRegistry)
    {
        resolver = new JsonResolver(schemaRegistry);
        syntaxValidator = new SyntaxValidator(keywordRegistry);
        keywordFactory = new KeywordFactory(keywordRegistry);
        cache = CacheBuilder.newBuilder().maximumSize(100L)
            .build(cacheLoader());
        formatAttributes = keywordRegistry.getFormatAttributes();
    }

    public Map<String, FormatAttribute> getFormatAttributes()
    {
        return formatAttributes;
    }

    public JsonValidator getValidator(final SchemaNode schemaNode)
    {
        return cache.getUnchecked(schemaNode);
    }

    /**
     * The cache loader function
     *
     * <p>The implemented {@link CacheLoader#load(Object)} method is the
     * critical part. It will try and check if ref resolution succeeds, if so it
     * checks the schema syntax, and finally it returns a validator.</p>
     *
     * <p>If any of the preliminary checks fail, it returns a {@link
     * FailingValidator}, else it returns an {@link InstanceValidator}.</p>
     *
     * @return the loader function
     */
    private CacheLoader<SchemaNode, JsonValidator> cacheLoader()
    {
        return new CacheLoader<SchemaNode, JsonValidator>()
        {
            @Override
            public JsonValidator load(final SchemaNode key)
            {
                // We can do that: we ask JacksonUtils to return an empty schema
                // each time we have to return one.
                if (key.getNode() == JacksonUtils.emptySchema())
                    return ALWAYS_TRUE;

                final SchemaNode realNode;

                try {
                    realNode = resolver.resolve(key);
                } catch (JsonSchemaException e) {
                    return new FailingValidator(e.getValidationMessage());
                }

                final List<Message> messages = Lists.newArrayList();

                syntaxValidator.validate(messages, realNode.getNode());

                if (!messages.isEmpty())
                    return new FailingValidator(messages);

                final Set<KeywordValidator> validators
                    = keywordFactory.getValidators(realNode.getNode());

                return new InstanceValidator(realNode, validators);
            }
        };
    }

    /**
     * Class instantiated when a schema node fails to pass ref resolution or
     * syntax checking
     *
     * @see #cacheLoader()
     */
    private static final class FailingValidator
        implements JsonValidator
    {
        private final List<Message> messages;

        private FailingValidator(final Message message)
        {
            messages = ImmutableList.of(message);
        }

        private FailingValidator(final List<Message> messages)
        {
            this.messages = ImmutableList.copyOf(messages);
        }

        @Override
        public void validate(final ValidationContext context,
            final ValidationReport report, final JsonNode instance)
        {
            report.addMessages(messages);
        }
    }
}
