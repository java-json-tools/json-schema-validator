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

import eel.kitchen.jsonschema.v2.keyword.ValidationStatus;
import eel.kitchen.jsonschema.v2.schema.ValidationState;
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

    private static final Map<String, Class<? extends SyntaxValidator>> checkers
        = new HashMap<String, Class<? extends SyntaxValidator>>();

    static {
        checkers.put("additionalItems", AdditionalItemsSyntaxValidator.class);
        checkers.put("additionalProperties", AdditionalPropertiesSyntaxValidator.class);
        checkers.put("dependencies", DependenciesSyntaxValidator.class);
        checkers.put("description", DescriptionSyntaxValidator.class);
        checkers.put("disallow", DisallowSyntaxValidator.class);
        checkers.put("divisibleBy", DivisibleBySyntaxValidator.class);
        checkers.put("$ref", DollarRefSyntaxValidator.class);
        checkers.put("$schema", DollarSchemaSyntaxValidator.class);
        checkers.put("enum", EnumSyntaxValidator.class);
        checkers.put("exclusiveMaximum", ExclusiveMaximumSyntaxValidator.class);
        checkers.put("exclusiveMinimum", ExclusiveMinimumSyntaxValidator.class);
        checkers.put("extends", ExtendsSyntaxValidator.class);
        checkers.put("format", FormatSyntaxValidator.class);
        checkers.put("id", IdSyntaxValidator.class);
        checkers.put("items", ItemsSyntaxValidator.class);
        checkers.put("maximum", MaximumSyntaxValidator.class);
        checkers.put("maxItems", MaxItemsSyntaxValidator.class);
        checkers.put("maxLength", MaxLengthSyntaxValidator.class);
        checkers.put("minimum", MinimumSyntaxValidator.class);
        checkers.put("minItems", MinItemsSyntaxValidator.class);
        checkers.put("minLength", MinLengthSyntaxValidator.class);
        checkers.put("pattern", PatternSyntaxValidator.class);
        checkers.put("patternProperties", PatternPropertiesSyntaxValidator.class);
        checkers.put("properties", PropertiesSyntaxValidator.class);
        checkers.put("title", TitleSyntaxValidator.class);
        checkers.put("type", TypeSyntaxValidator.class);
        checkers.put("uniqueItems", UniqueItemsSyntaxValidator.class);
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
        if (schema == null)
            return Arrays.asList("schema is null");

        if (!schema.isObject())
            return Arrays.asList("JSON document is not a schema");

        final Set<String> keywords = CollectionUtils.toSet(schema
            .getFieldNames());

        SyntaxValidator checker;

        final ValidationState state = new ValidationState();
        final ValidationState tmp = new ValidationState();


        for (final String keyword: keywords) {
            if (!checkers.containsKey(keyword)) {
                state.addMessage("unknown keyword " + keyword);
                state.setStatus(ValidationStatus.FAILURE);
                continue;
            }
            checker = getChecker(keyword);
            tmp.clearMessages();
            checker.validate(tmp, schema);
            if (tmp.isFailure()) {
                state.setStatus(ValidationStatus.FAILURE);
                state.addMessages(tmp.getMessages());
            }
        }

        return Collections.unmodifiableList(state.getMessages());
    }

    private static SyntaxValidator getChecker(final String keyword)
    {
        final Class<? extends SyntaxValidator> c = checkers.get(keyword);
        final Constructor<? extends SyntaxValidator> constructor;

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

    private static SyntaxValidator failure(final String keyword,
        final Exception e)
    {
        return new SyntaxValidator()
        {
            @Override
            public void validate(final ValidationState state,
                final JsonNode schema)
            {
                state.addMessage(String.format("cannot instantiate " +
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
