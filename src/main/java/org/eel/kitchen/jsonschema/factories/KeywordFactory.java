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
import org.eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import org.eel.kitchen.jsonschema.base.Validator;
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
    private final Map<String, KeywordValidator> validators
        = new HashMap<String, KeywordValidator>();

    /**
     * Set of ignored keywords (for which validation is always true)
     */
    private final Set<String> ignoredKeywords = new HashSet<String>();

    /**
     * Constructor; registers validators using
     * {@link #registerValidator(String, Class, NodeType...)}.
     */
    public KeywordFactory()
    {
        register("additionalItems", new AdditionalItemsKeywordValidator(),
            ARRAY);
        register("additionalProperties",
            new AdditionalPropertiesKeywordValidator(), OBJECT);
        register("dependencies", new DependenciesKeywordValidator(),
            NodeType.values());
        register("disallow", new DisallowKeywordValidator(), NodeType.values());
        register("divisibleBy", new DivisibleByKeywordValidator(), INTEGER,
            NUMBER);
        register("enum", new EnumKeywordValidator(), NodeType.values());
        register("extends", new ExtendsKeywordValidator(), NodeType.values());
        register("format", new FormatKeywordValidator(), NodeType.values());
        register("maximum", new MaximumKeywordValidator(), INTEGER, NUMBER);
        register("maxItems", new MaxItemsKeywordValidator(), ARRAY);
        register("maxLength", new MaxLengthKeywordValidator(), STRING);
        register("minimum", new MinimumKeywordValidator(), INTEGER, NUMBER);
        register("minItems", new MinItemsKeywordValidator(), ARRAY);
        register("minLength", new MinLengthKeywordValidator(), STRING);
        register("pattern", new PatternKeywordValidator(), STRING);
        register("properties", new PropertiesKeywordValidator(), OBJECT);
        register("type", new TypeKeywordValidator(), NodeType.values());
        register("uniqueItems", new UniqueItemsKeywordValidator(), ARRAY);
        register("$ref", new RefKeywordValidator(), NodeType.values());

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
     * @param c the {@link KeywordValidator} as a {@link Class} object
     * @param types the instance types this validator can handle
     * @throws IllegalArgumentException if the keyword is already registerd,
     * or if the {@code types} array is empty
     *
     * @see SyntaxFactory#registerValidator(String, Class)
     * @see JsonValidator#registerValidator(String, Class, Class, NodeType...)
     */
    public void registerValidator(final String keyword,
        final Class<? extends KeywordValidator> c, final NodeType... types)
    {
        if (ignoredKeywords.contains(keyword) || validators.containsKey(keyword))
            throw new IllegalArgumentException("keyword already registered");

        if (c == null) {
            ignoredKeywords.add(keyword);
            return;
        }

        if (types.length == 0)
            throw new IllegalArgumentException("cannot register a new keyword"
                + " with no JSON type to match against");

        final EnumSet<NodeType> typeset = EnumSet.copyOf(Arrays.asList(types));

        final KeywordValidator kv;

        Exception exception;

        try {
            kv = buildValidator(c);
            fieldMap.put(keyword, typeset);
            validators.put(keyword, kv);
            return;
        } catch (NoSuchMethodException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InstantiationException e) {
            exception = e;
        }

        final String errmsg = String.format("cannot instantiate validator: "
            + "%s: %s", exception.getClass().getName(), exception.getMessage());

        throw new IllegalArgumentException(errmsg);
    }

    private void register(final String keyword, final KeywordValidator kv,
        final NodeType... types)
    {
        final EnumSet<NodeType> typeset = EnumSet.copyOf(Arrays.asList(types));

        fieldMap.put(keyword, typeset);
        validators.put(keyword, kv);

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

    public Collection<Validator> getValidators(
        final ValidationContext context, final JsonNode instance)
    {
        final NodeType type = NodeType.getNodeType(instance);
        final Set<Validator> ret
            = new LinkedHashSet<Validator>();

        final JsonNode schemaNode = context.getSchemaNode();

        final Set<String> keywords
            = CollectionUtils.toSet(schemaNode.getFieldNames());

        if (keywords.isEmpty())
            return Arrays.<Validator>asList(
                new AlwaysTrueValidator());

        final Set<String> keyset = new HashSet<String>();

        keyset.addAll(validators.keySet());
        keyset.retainAll(keywords);

        for (final String key: keyset) {
            if (!fieldMap.get(key).contains(type))
                continue;
            ret.add(validators.get(key));
        }

        return Collections.unmodifiableSet(ret);
    }

    /**
     * Build a validator given a class
     *
     * @param c the class object
     * @return the validator
     * @throws NoSuchMethodException constructor was not found
     * @throws InvocationTargetException see {@link InvocationTargetException}
     * @throws IllegalAccessException see {@link IllegalAccessException}
     * @throws InstantiationException see {@link InstantiationException}
     */
    private static KeywordValidator buildValidator(
        final Class<? extends KeywordValidator> c)
        throws NoSuchMethodException, InvocationTargetException,
        IllegalAccessException, InstantiationException
    {
        final Constructor<? extends KeywordValidator> constructor
            = c.getConstructor();

        return constructor.newInstance();
    }
}
