/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.factories;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import eel.kitchen.jsonschema.base.MatchAllValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.container.ArrayValidator;
import eel.kitchen.jsonschema.container.ObjectValidator;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.jsonschema.keyword.AdditionalItemsKeywordValidator;
import eel.kitchen.jsonschema.keyword.AdditionalPropertiesKeywordValidator;
import eel.kitchen.jsonschema.keyword.AlwaysTrueKeywordValidator;
import eel.kitchen.jsonschema.keyword.DependenciesKeywordValidator;
import eel.kitchen.jsonschema.keyword.DisallowKeywordValidator;
import eel.kitchen.jsonschema.keyword.DivisibleByKeywordValidator;
import eel.kitchen.jsonschema.keyword.EnumKeywordValidator;
import eel.kitchen.jsonschema.keyword.ExtendsKeywordValidator;
import eel.kitchen.jsonschema.keyword.FormatKeywordValidator;
import eel.kitchen.jsonschema.keyword.KeywordValidator;
import eel.kitchen.jsonschema.keyword.MaxItemsKeywordValidator;
import eel.kitchen.jsonschema.keyword.MaxLengthKeywordValidator;
import eel.kitchen.jsonschema.keyword.MaximumKeywordValidator;
import eel.kitchen.jsonschema.keyword.MinItemsKeywordValidator;
import eel.kitchen.jsonschema.keyword.MinLengthKeywordValidator;
import eel.kitchen.jsonschema.keyword.MinimumKeywordValidator;
import eel.kitchen.jsonschema.keyword.PatternKeywordValidator;
import eel.kitchen.jsonschema.keyword.PropertiesKeywordValidator;
import eel.kitchen.jsonschema.keyword.RefKeywordValidator;
import eel.kitchen.jsonschema.keyword.TypeKeywordValidator;
import eel.kitchen.jsonschema.keyword.UniqueItemsKeywordValidator;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static eel.kitchen.util.NodeType.*;

/**
 * Factory for keyword validators, ie the core of validation.
 */
public final class KeywordFactory
{
    /**
     * Map pairing a schema keyword and the instance types it applies to
     */
    private final Map<String, EnumSet<NodeType>> fieldMap
        = new HashMap<String, EnumSet<NodeType>>();

    /**
     * Map pairing a schema keyword and the matching {@link KeywordValidator}
     * as a {@link Class}
     */
    private final Map<String, Class<? extends KeywordValidator>> validators
        = new HashMap<String, Class<? extends KeywordValidator>>();

    /**
     * Constructor; registers validators using
     * {@link #registerValidator(String, Class, NodeType...)}.
     */
    public KeywordFactory()
    {
        registerValidator("additionalItems",
            AdditionalItemsKeywordValidator.class, ARRAY);
        registerValidator("additionalProperties",
            AdditionalPropertiesKeywordValidator.class, OBJECT);
        registerValidator("dependencies", DependenciesKeywordValidator.class,
            NodeType.values());
        registerValidator("disallow", DisallowKeywordValidator.class,
            NodeType.values());
        registerValidator("divisibleBy", DivisibleByKeywordValidator.class,
            INTEGER, NUMBER);
        registerValidator("enum", EnumKeywordValidator.class,
            NodeType.values());
        registerValidator("extends", ExtendsKeywordValidator.class,
            NodeType.values());
        registerValidator("format", FormatKeywordValidator.class,
            NodeType.values());
        registerValidator("items", AlwaysTrueKeywordValidator.class, ARRAY);
        registerValidator("maximum", MaximumKeywordValidator.class, INTEGER,
            NUMBER);
        registerValidator("maxItems", MaxItemsKeywordValidator.class, ARRAY);
        registerValidator("maxLength", MaxLengthKeywordValidator.class, STRING);
        registerValidator("minimum", MinimumKeywordValidator.class, INTEGER,
            NUMBER);
        registerValidator("minItems", MinItemsKeywordValidator.class, ARRAY);
        registerValidator("minLength", MinLengthKeywordValidator.class, STRING);
        registerValidator("pattern", PatternKeywordValidator.class, STRING);
        registerValidator("patternProperties", AlwaysTrueKeywordValidator.class,
            OBJECT);
        registerValidator("properties", PropertiesKeywordValidator.class,
            OBJECT);
        registerValidator("type", TypeKeywordValidator.class,
            NodeType.values());
        registerValidator("uniqueItems", UniqueItemsKeywordValidator.class,
            ARRAY);
        registerValidator("$ref", RefKeywordValidator.class, NodeType.values());
    }

    /**
     * Register one validator for a given keyword
     *
     * @param field the keyword
     * @param v the {@link KeywordValidator} as a {@link Class} object
     * @param types the instance types this validator can handle
     */
    private void registerValidator(final String field,
        final Class<? extends KeywordValidator> v, final NodeType... types)
    {
        final EnumSet<NodeType> typeset = EnumSet.copyOf(Arrays.asList(types));

        fieldMap.put(field, typeset);
        validators.put(field, v);
    }

    /**
     * Get a validator (a {@link KeywordValidator} really) for the given
     * context and instance to validate. Only called from {@link
     * ValidationContext#getValidator(JsonNode)}.
     *
     * @param context the current validation context
     * @param instance the instance to validate
     * @return the validator
     */
    public Validator getValidator(final ValidationContext context,
        final JsonNode instance)
    {
        final Collection<Validator> collection
            = getValidators(context, instance);


        final Validator validator;
        switch (collection.size()) {
            case 0:
                return new AlwaysTrueKeywordValidator(context, instance);
            case 1:
                validator = collection.iterator().next();
                break;
            default:
                validator = new MatchAllValidator(context, collection);
        }

        if (!instance.isContainerNode())
            return validator;

        return instance.isArray()
            ? new ArrayValidator(validator, context, instance)
            : new ObjectValidator(validator, context, instance);
    }


    /**
     * Get a collection of validators for the context and instance,
     * by grabbing the schema node using
     * {@link ValidationContext#getSchemaNode()} and grabbing validators from
     * the {@link #fieldMap} and {@link #validators} maps. Will return an
     * {@link AlwaysTrueKeywordValidator} if no validators are found (ie,
     * none of the keywords of the schema node can validate the instance
     * type), and an {@link AlwaysFalseValidator} if one validator fails to
     * instantiate (see
     * {@link #buildValidator(Class, ValidationContext, JsonNode)}).
     *
     * @param context the validation context
     * @param instance the instance
     * @return the list of validators as a {@link Collection}
     */
    private Collection<Validator> getValidators(
        final ValidationContext context, final JsonNode instance)
    {
        final NodeType type = NodeType.getNodeType(instance);
        final Set<Validator> ret = new LinkedHashSet<Validator>();

        final JsonNode schemaNode = context.getSchemaNode();

        final Set<String> keywords
            = CollectionUtils.toSet(schemaNode.getFieldNames());

        if (keywords.isEmpty())
            return Arrays.<Validator>asList(new AlwaysTrueKeywordValidator(
                context, instance));

        final Set<String> keyset = new HashSet<String>();

        Class<? extends Validator> c;

        keyset.addAll(validators.keySet());
        keyset.retainAll(keywords);

        Validator validator;

        for (final String key: keyset) {
            if (!fieldMap.get(key).contains(type))
                continue;
            c = validators.get(key);
            try {
                validator = buildValidator(c, context, instance);
                ret.add(validator);
            } catch (Exception e) {
                final String message = "Cannot instantiate validator "
                    + "for keyword " + key + ": " + e.getClass().getName();
                final ValidationReport report = context.createReport();
                report.addMessage(message);
                validator = new AlwaysFalseValidator(report);
                return Arrays.asList(validator);
            }
        }

        return Collections.unmodifiableSet(ret);
    }

    /**
     * Build a validator given a class, context and instance.
     *
     * @param c the class object
     * @param context the context
     * @param instance the instance
     * @return the validator
     * @throws NoSuchMethodException constructor was not found
     * @throws InvocationTargetException see {@link InvocationTargetException}
     * @throws IllegalAccessException see {@link IllegalAccessException}
     * @throws InstantiationException see {@link InstantiationException}
     */
    private static Validator buildValidator(final Class<? extends Validator> c,
        final ValidationContext context, final JsonNode instance)
        throws NoSuchMethodException, InvocationTargetException,
        IllegalAccessException, InstantiationException
    {
        final Constructor<? extends Validator> constructor
            = c.getConstructor(ValidationContext.class, JsonNode.class);

        return constructor.newInstance(context, instance);
    }
}
