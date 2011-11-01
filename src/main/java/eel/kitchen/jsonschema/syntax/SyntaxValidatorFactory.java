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

package eel.kitchen.jsonschema.syntax;

import eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import eel.kitchen.jsonschema.base.MatchAllValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SyntaxValidatorFactory
{
    private final Map<String, Class<? extends Validator>> validators
        = new HashMap<String, Class<? extends Validator>>();

    public SyntaxValidatorFactory()
    {
        validators.put("additionalItems", AdditionalItemsSyntaxValidator.class);
        validators.put("additionalProperties",
            AdditionalPropertiesSyntaxValidator.class);
        validators.put("dependencies", DependenciesSyntaxValidator.class);
        validators.put("description", DescriptionSyntaxValidator.class);
        validators.put("disallow", DisallowSyntaxValidator.class);
        validators.put("divisibleBy", DivisibleBySyntaxValidator.class);
        validators.put("$ref", DollarRefSyntaxValidator.class);
        validators.put("$schema", DollarSchemaSyntaxValidator.class);
        validators.put("enum", EnumSyntaxValidator.class);
        validators.put("exclusiveMaximum", ExclusiveMaximumSyntaxValidator.class);
        validators.put("exclusiveMinimum", ExclusiveMinimumSyntaxValidator.class);
        validators.put("extends", ExtendsSyntaxValidator.class);
        validators.put("format", FormatSyntaxValidator.class);
        validators.put("id", IdSyntaxValidator.class);
        validators.put("items", ItemsSyntaxValidator.class);
        validators.put("maximum", MaximumSyntaxValidator.class);
        validators.put("maxItems", MaxItemsSyntaxValidator.class);
        validators.put("maxLength", MaxLengthSyntaxValidator.class);
        validators.put("minimum", MinimumSyntaxValidator.class);
        validators.put("minItems", MinItemsSyntaxValidator.class);
        validators.put("minLength", MinLengthSyntaxValidator.class);
        validators.put("pattern", PatternSyntaxValidator.class);
        validators.put("patternProperties", PatternPropertiesSyntaxValidator.class);
        validators.put("properties", PropertiesSyntaxValidator.class);
        validators.put("title", TitleSyntaxValidator.class);
        validators.put("type", TypeSyntaxValidator.class);
        validators.put("uniqueItems", UniqueItemsSyntaxValidator.class);
    }

    public Validator getValidator(final JsonNode schema)
    {
        if (schema == null)
            return new AlwaysFalseValidator("schema is null");

        if (!schema.isObject())
            return new AlwaysFalseValidator("not a valid schema (not an "
                + "object)");

        final Set<String> fieldSet
            = CollectionUtils.toSet(schema.getFieldNames());

        final Set<String> keywords = new HashSet<String>(validators.keySet());

        if (!keywords.containsAll(fieldSet)) {
            fieldSet.removeAll(keywords);
            final List<String> messages = new LinkedList<String>();
            for (final String field: fieldSet)
                messages.add("unknown keyword " + field);
            return new AlwaysFalseValidator(messages);
        }

        fieldSet.retainAll(keywords);

        if (fieldSet.isEmpty())
            return new AlwaysTrueValidator();

        final Collection<Validator> collection = getValidators(fieldSet, schema);

        if (collection.size() == 1)
            return collection.iterator().next();

        return new MatchAllValidator(collection);
    }

    private Collection<Validator> getValidators(final Set<String> fieldSet,
        final JsonNode schema)
    {
        final Set<Validator> ret = new HashSet<Validator>();

        Class<? extends Validator> c;
        Validator v;

        for (final String field: fieldSet) {
            c = validators.get(field);
            try {
                v = buildValidator(c, schema);
                ret.add(v);
            } catch (Exception e) {
                v = new AlwaysFalseValidator("cannot instantiate syntax "
                    + "validator for " + field + ": " + e.getClass().getName()
                    + ": " + e.getMessage());
                return Arrays.asList(v);
            }
        }

        return ret;
    }

    private static Validator buildValidator(final Class<? extends Validator> c,
        final JsonNode schema)
        throws NoSuchMethodException, InvocationTargetException,
        IllegalAccessException, InstantiationException
    {
        final Constructor<? extends Validator> constructor
            = c.getConstructor(JsonNode.class);

        return constructor.newInstance(schema);
    }
}
