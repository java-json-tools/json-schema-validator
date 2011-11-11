/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.factories;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.JsonValidator;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.base.AbstractValidator;
import org.eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import org.eel.kitchen.jsonschema.base.MatchAllValidator;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.container.ArrayValidator;
import org.eel.kitchen.jsonschema.container.ObjectValidator;
import org.eel.kitchen.jsonschema.context.ValidationContext;
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
import org.eel.kitchen.jsonschema.keyword.RefKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.TypeKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.UniqueItemsKeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

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

import static org.eel.kitchen.util.NodeType.*;

/**
 * Factory for keyword validators, ie the core of the validation process
 *
 * <p>Keyword validators all register to this factory, with the list of types
 * they can validate. They can be pretty confident that their validation data
 * is correct, since syntax validation will have ensured this. Therefore they
 * only have to worry about validating instances.</p>
 *
 * <p>Most keyword validators are deterministic and can tell right on if
 * their instance is valid. However, some validators cannot and need to spawn
 * other validators. This is the case for keywords like {@code dependencies}
 * for instance.</p>
 *
 * @see SyntaxFactory
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

    private final Set<String> ignoredKeywords = new HashSet<String>();

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
        registerValidator("maximum", MaximumKeywordValidator.class, INTEGER,
            NUMBER);
        registerValidator("maxItems", MaxItemsKeywordValidator.class, ARRAY);
        registerValidator("maxLength", MaxLengthKeywordValidator.class,
            STRING);
        registerValidator("minimum", MinimumKeywordValidator.class, INTEGER,
            NUMBER);
        registerValidator("minItems", MinItemsKeywordValidator.class, ARRAY);
        registerValidator("minLength", MinLengthKeywordValidator.class,
            STRING);
        registerValidator("pattern", PatternKeywordValidator.class, STRING);
        registerValidator("properties", PropertiesKeywordValidator.class,
            OBJECT);
        registerValidator("type", TypeKeywordValidator.class, NodeType.values());
        registerValidator("uniqueItems", UniqueItemsKeywordValidator.class,
            ARRAY);
        registerValidator("$ref", RefKeywordValidator.class, NodeType.values());

        ignoredKeywords.add("items");
        ignoredKeywords.add("patternProperties");
    }

    /**
     * Register one validator for a given keyword
     *
     * <p>If the validator argument is {@code null}, this means no validation
     * will be performed at all. If it is not null, however,
     * be sure to pair it with a {@link SyntaxValidator},
     * since it is the latter which will ensure that on invocation,
     * the new validator will not fail due to incorrect input.
     * </p>
     *
     * @param keyword the keyword
     * @param v the {@link KeywordValidator} as a {@link Class} object
     * @param types the instance types this validator can handle
     * @throws IllegalArgumentException if the keyword is already registerd,
     * or if the {@code types} array is empty
     *
     * @see SyntaxFactory#registerValidator(String, Class)
     * @see JsonValidator#registerValidator(String, Class, Class, NodeType...)
     */
    public void registerValidator(final String keyword,
        final Class<? extends KeywordValidator> v, final NodeType... types)
    {
        if (ignoredKeywords.contains(keyword) || validators.containsKey(keyword))
            throw new IllegalArgumentException("keyword already registered to"
                + " that KeywordFactory");

        if (v == null) {
            ignoredKeywords.add(keyword);
            return;
        }

        if (types.length == 0)
            throw new IllegalArgumentException("cannot register a new keyword"
                + " with no JSON type to match against");

        final EnumSet<NodeType> typeset = EnumSet.copyOf(Arrays.asList(types));

        fieldMap.put(keyword, typeset);
        validators.put(keyword, v);
    }

    /**
     * Unregister a validator for the given keyword
     *
     * <p>This has basically</p>
     * @param keyword the victim
     */
    public void unregisterValidator(final String keyword)
    {
        ignoredKeywords.remove(keyword);
        fieldMap.remove(keyword);
        validators.remove(keyword);
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
                validator = AbstractValidator.TRUE;
                break;
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
     * the {@link #fieldMap} and {@link #validators} maps. Will return
     * {@link AbstractValidator#TRUE} if no validators are found (ie,
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
            return Arrays.asList(AbstractValidator.TRUE);

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
