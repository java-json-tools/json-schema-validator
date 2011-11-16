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
import org.eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import org.eel.kitchen.jsonschema.base.Validator;
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
import org.eel.kitchen.jsonschema.main.JsonValidator;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
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
     * Set of ignored keywords (for which validation is always true)
     */
    private final Set<String> ignoredKeywords = new HashSet<String>();

    /**
     * Map of all validators
     *
     * <p>The key is the node type, the value is itself a map pairing keywords
     * with their matching validators.</p>
     */
    private final Map<NodeType, Map<String, KeywordValidator>> validators
        = new EnumMap<NodeType, Map<String, KeywordValidator>>(NodeType.class);

    /**
     * Constructor; registers validators using
     * {@link #register(String, KeywordValidator, NodeType...)}
     */
    public KeywordFactory()
    {
        for (final NodeType type: NodeType.values())
            validators.put(type, new HashMap<String, KeywordValidator>());

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
     * the new validator will not fail (or crash) due to incorrect input.
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
        /*
         * Both tests below are for security only... They should not happen
         * until the "no schema validation" feature is implemented.
         */
        if (ignoredKeywords.contains(keyword))
            throw new IllegalArgumentException("keyword already registered");

        for (final NodeType type: types)
            if (validators.get(type).keySet().contains(keyword))
                throw new IllegalArgumentException("keyword already registered");

        if (c == null) {
            ignoredKeywords.add(keyword);
            return;
        }

        if (types.length == 0)
            throw new IllegalArgumentException("cannot register a new keyword"
                + " with no JSON type to match against");

        final KeywordValidator kv;

        Exception exception;

        try {
            kv = buildValidator(c);
            register(keyword, kv, types);
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

    /**
     * Private validator registration method
     *
     * <p>The only difference with {@link #registerValidator(String, Class,
     * NodeType...)} is that here the validator is instantiated.</p>
     *
     * @param keyword the keyword to register
     * @param kv the validator
     * @param types the type set validable by this validator
     */
    private void register(final String keyword, final KeywordValidator kv,
        final NodeType... types)
    {
        if (kv == null) {
            ignoredKeywords.add(keyword);
            return;
        }

        for (final NodeType type: types)
            validators.get(type).put(keyword, kv);
    }

    /**
     * Unregister a validator for the given keyword
     *
     * @param keyword the victim
     */
    public void unregisterValidator(final String keyword)
    {
        ignoredKeywords.remove(keyword);
        for (final NodeType type: NodeType.values())
            validators.get(type).remove(keyword);
    }

    /**
     * Get a validator set for a given context and instance
     *
     * @param context the context
     * @param instance the instance to be validated
     * @return the matching set of validators
     */
    public Collection<Validator> getValidators(
        final ValidationContext context, final JsonNode instance)
    {
        final JsonNode schemaNode = context.getSchemaNode();
        final Set<String> keywords
            = CollectionUtils.toSet(schemaNode.getFieldNames());

        final NodeType type = NodeType.getNodeType(instance);
        final Map<String, Validator> map
            = new HashMap<String, Validator>(validators.get(type));

        map.keySet().retainAll(keywords);

        if (map.isEmpty())
            return Arrays.<Validator>asList(new AlwaysTrueValidator());

        return Collections.unmodifiableCollection(map.values());
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
