/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.v2.check;

import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SchemaChecker
{
    private static final SchemaChecker instance = new SchemaChecker();

    private static final Map<String, Class<? extends KeywordChecker>> checkers
        = new HashMap<String, Class<? extends KeywordChecker>>();

    static {
        checkers.put("additionalItems", AdditionalItemsKeywordChecker.class);
        checkers.put("additionalProperties", AdditionalPropertiesKeywordChecker.class);
        checkers.put("dependencies", DependenciesKeywordChecker.class);
        checkers.put("description", DescriptionKeywordChecker.class);
        checkers.put("disallow", DisallowKeywordChecker.class);
        checkers.put("divisibleBy", DivisibleByKeywordChecker.class);
        checkers.put("$ref", DollarRefKeywordChecker.class);
        checkers.put("$schema", DollarSchemaKeywordChecker.class);
        checkers.put("enum", EnumKeywordChecker.class);
        checkers.put("exclusiveMaximum", ExclusiveMaximumKeywordChecker.class);
        checkers.put("exclusiveMinimum", ExclusiveMinimumKeywordChecker.class);
        checkers.put("extends", ExtendsKeywordChecker.class);
        checkers.put("format", FormatKeywordChecker.class);
        checkers.put("id", IdKeywordChecker.class);
        checkers.put("items", ItemsKeywordChecker.class);
        checkers.put("maximum", MaximumKeywordChecker.class);
        checkers.put("maxItems", MaxItemsKeywordChecker.class);
        checkers.put("maxLength", MaxLengthKeywordChecker.class);
        checkers.put("minimum", MinimumKeywordChecker.class);
        checkers.put("minItems", MinItemsKeywordChecker.class);
        checkers.put("minLength", MinLengthKeywordChecker.class);
        checkers.put("pattern", PatternKeywordChecker.class);
        checkers.put("patternProperties", PatternPropertiesKeywordChecker.class);
        checkers.put("properties", PropertiesKeywordChecker.class);
        checkers.put("title", TitleKeywordChecker.class);
        checkers.put("type", TypeKeywordChecker.class);
        checkers.put("uniqueItems", UniqueItemsKeywordChecker.class);
    }

    private SchemaChecker()
    {
    }

    public static SchemaChecker getInstance()
    {
        return instance;
    }

    public List<String> check(final JsonNode schema)
    {
        final List<String> ret = new LinkedList<String>();

        if (schema == null)
            return Arrays.asList("schema is null");

        if (!schema.isObject())
            return Arrays.asList("JSON document is not a schema");

        final Set<String> keywords = CollectionUtils.toSet(schema
            .getFieldNames());

        KeywordChecker checker;

        for (final String keyword: keywords) {
            if (!checkers.containsKey(keyword)) {
                ret.add("unknown keyword " + keyword);
                continue;
            }
            checker = getChecker(keyword);
            if (checker.validate(schema))
                continue;
            ret.addAll(checker.getMessages());
        }

        return Collections.unmodifiableList(ret);
    }

    private static KeywordChecker getChecker(final String keyword)
    {
        final Class<? extends KeywordChecker> c = checkers.get(keyword);
        final Constructor<? extends KeywordChecker> constructor;

        try {
            constructor = c.getConstructor();
        } catch (NoSuchMethodException e) {
            return failure(keyword, e);
        }

        try {
            return constructor.newInstance();
        } catch (InvocationTargetException e) {
            return failure(keyword, e);
        } catch (InstantiationException e) {
            return failure(keyword, e);
        } catch (IllegalAccessException e) {
            return failure(keyword, e);
        }
    }

    private static KeywordChecker failure(final String keyword,
        final Exception e)
    {
        return new KeywordChecker()
        {
            @Override
            public boolean validate(final JsonNode schema)
            {
                return false;
            }

            @Override
            public List<String> getMessages()
            {
                return Arrays.asList(String.format("cannot instantiate " +
                    "checker for keyword %s: %s: %s", keyword,
                    e.getClass().getName(), e.getMessage()));
            }
        };
    }

    public static void main(final String... args)
        throws IOException
    {
        final JsonNode schema = new ObjectMapper().readTree("{ "
            + "\"exclusiveMaximum\": true}");

        for (final String msg: SchemaChecker.getInstance().check(schema))
            System.out.println(msg);
    }
}
