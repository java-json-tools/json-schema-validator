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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.keyword.AdditionalItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.AdditionalPropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.DependenciesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.DisallowKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.DivisibleByKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.EnumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.ExtendsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.FormatKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MaxItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MaxLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MaximumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MinItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MinLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MinimumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.PatternKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.PropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.TypeKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.UniqueItemsKeywordValidator;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class KeywordFactory
{
    private static final KeywordFactory instance = new KeywordFactory();

    private static final Map<String, Class<? extends KeywordValidator>>
        validators = new HashMap<String, Class<? extends KeywordValidator>>();

    static {
        validators.put("additionalItems",
            AdditionalItemsKeywordValidator.class);
        validators.put("additionalProperties",
            AdditionalPropertiesKeywordValidator.class);
        validators.put("dependencies", DependenciesKeywordValidator.class);
        validators.put("disallow", DisallowKeywordValidator.class);
        validators.put("divisibleBy", DivisibleByKeywordValidator.class);
        validators.put("enum", EnumKeywordValidator.class);
        validators.put("extends", ExtendsKeywordValidator.class);
        validators.put("format", FormatKeywordValidator.class);
        validators.put("maximum", MaximumKeywordValidator.class);
        validators.put("maxItems", MaxItemsKeywordValidator.class);
        validators.put("maxLength", MaxLengthKeywordValidator.class);
        validators.put("minimum", MinimumKeywordValidator.class);
        validators.put("minItems", MinItemsKeywordValidator.class);
        validators.put("minLength", MinLengthKeywordValidator.class);
        validators.put("pattern", PatternKeywordValidator.class);
        validators.put("properties", PropertiesKeywordValidator.class);
        validators.put("type", TypeKeywordValidator.class);
        validators.put("uniqueItems", UniqueItemsKeywordValidator.class);
    }

    public static KeywordFactory getInstance()
    {
        return instance;
    }

    private KeywordFactory()
    {
    }

    public Set<KeywordValidator> getValidators(final JsonNode schema)
    {
        final Set<KeywordValidator> ret = new HashSet<KeywordValidator>();

        final Set<String> set = CollectionUtils.toSet(schema.fieldNames());

        set.retainAll(validators.keySet());

        for (final String keyword: set)
            ret.add(buildValidator(validators.get(keyword), schema));

        return ret;
    }

    private KeywordValidator buildValidator(final Class<? extends KeywordValidator> c,
        final JsonNode schema)
    {
        final Constructor<? extends KeywordValidator> constructor;

        try {
            constructor = c.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException e) {
            return invalidValidator(e);
        }

        try {
            return constructor.newInstance(schema);
        } catch (InstantiationException e) {
            return invalidValidator(e);
        } catch (IllegalAccessException e) {
            return invalidValidator(e);
        } catch (InvocationTargetException e) {
            return invalidValidator(e);
        }
    }

    private KeywordValidator invalidValidator(final Exception e)
    {
        return new KeywordValidator(NodeType.values())
        {
            @Override
            protected void validate(final ValidationReport report,
                final JsonNode instance)
            {
                report.addMessage("cannot build validator: "
                    + e.getClass().getName() + ": " + e.getMessage());
            }
        };
    }
}
