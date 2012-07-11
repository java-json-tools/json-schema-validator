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
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class JsonValidatorFactory
{
    private final LoadingCache<JsonNode, JsonValidator> cache;

    public JsonValidatorFactory(final KeywordBundle bundle)
    {
        cache = CacheBuilder.newBuilder()
            .maximumSize(100).build(new ValidatorBuilder(bundle));
    }

    private static class ValidatorBuilder
        extends CacheLoader<JsonNode, JsonValidator>
    {
        private final SyntaxValidator syntaxValidator;
        private final KeywordFactory keywordFactory;

        ValidatorBuilder(final KeywordBundle bundle)
        {
            syntaxValidator = new SyntaxValidator(bundle);
            keywordFactory = new KeywordFactory(bundle);
        }

        @Override
        public JsonValidator load(final JsonNode key)
        {
            final List<String> messages = new ArrayList<String>();
            syntaxValidator.validate(messages, key);

            if (!messages.isEmpty())
                return new InvalidJsonValidator(messages);

            final Set<KeywordValidator> validators
                = keywordFactory.getValidators(key);

            return new SimpleJsonValidator(validators);
        }
    }

    public JsonValidator fromNode(final JsonNode schemaNode)
    {
        return cache.getUnchecked(schemaNode);
    }
}
