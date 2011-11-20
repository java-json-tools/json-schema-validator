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
import org.eel.kitchen.jsonschema.keyword.common.AdditionalItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.AdditionalPropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.DependenciesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.DisallowKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.DivisibleByKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.EnumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.ExtendsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.FormatKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MaxItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MaxLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MaximumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MinItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MinLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MinimumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.PatternKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv3.PropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.RefKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.TypeKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.UniqueItemsKeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonValidator;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
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
    private final Map<NodeType, Set<String>> ignoredKeywords
        = new EnumMap<NodeType, Set<String>>(NodeType.class);

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
     * {@link #registerValidator(String, KeywordValidator, NodeType...)}
     */
    public KeywordFactory()
    {
        for (final NodeType type: NodeType.values()) {
            validators.put(type, new HashMap<String, KeywordValidator>());
            ignoredKeywords.put(type, new HashSet<String>());
        }

        registerValidator("additionalItems",
            new AdditionalItemsKeywordValidator(), ARRAY);
        registerValidator("additionalProperties",
            new AdditionalPropertiesKeywordValidator(), OBJECT);
        registerValidator("dependencies", new DependenciesKeywordValidator(),
            NodeType.values());
        registerValidator("disallow", new DisallowKeywordValidator(),
            NodeType.values());
        registerValidator("divisibleBy", new DivisibleByKeywordValidator(),
            INTEGER, NUMBER);
        registerValidator("enum", new EnumKeywordValidator(),
            NodeType.values());
        registerValidator("extends", new ExtendsKeywordValidator(),
            NodeType.values());
        registerValidator("format", new FormatKeywordValidator(),
            NodeType.values());
        registerValidator("maximum", new MaximumKeywordValidator(), INTEGER,
            NUMBER);
        registerValidator("maxItems", new MaxItemsKeywordValidator(), ARRAY);
        registerValidator("maxLength", new MaxLengthKeywordValidator(), STRING);
        registerValidator("minimum", new MinimumKeywordValidator(), INTEGER,
            NUMBER);
        registerValidator("minItems", new MinItemsKeywordValidator(), ARRAY);
        registerValidator("minLength", new MinLengthKeywordValidator(), STRING);
        registerValidator("pattern", new PatternKeywordValidator(), STRING);
        registerValidator("properties", new PropertiesKeywordValidator(),
            OBJECT);
        registerValidator("type", new TypeKeywordValidator(),
            NodeType.values());
        registerValidator("uniqueItems", new UniqueItemsKeywordValidator(),
            ARRAY);
        registerValidator("$ref", new RefKeywordValidator(), NodeType.values());

        registerValidator("items", null, ARRAY);
        registerValidator("patternProperties", null, OBJECT);
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
     * @param kv the {@link KeywordValidator} as a {@link Class} object
     * @param types the instance types this validator can handle
     * @throws IllegalArgumentException if the keyword is already registerd,
     * or if the {@code types} array is empty
     *
     * @see SyntaxFactory#registerValidator(String, SyntaxValidator)
     * @see JsonValidator#registerValidator(String, SyntaxValidator,
     * KeywordValidator, NodeType...)
     */
    public void registerValidator(final String keyword,
        final KeywordValidator kv, final NodeType... types)
    {
        /*
         * Both tests below are for security only... They should not happen
         * until the "no schema validation" feature is implemented.
         */

        for (final NodeType type: types) {
            if (ignoredKeywords.get(type).contains(keyword))
                throw new IllegalArgumentException("keyword already registered");
            if (validators.get(type).keySet().contains(keyword))
                throw new IllegalArgumentException(
                    "keyword already registered");
        }

        if (types.length == 0)
            throw new IllegalArgumentException("cannot register a new keyword"
                + " with no JSON type to match against");

        if (kv == null) {
            for (final NodeType type: types)
                ignoredKeywords.get(type).add(keyword);
            return;
        }

        for (final NodeType type: types)
            validators.get(type).put(keyword, kv);
    }

    /**
     * Unregister a validator for the given keyword
     *
     * @param keyword the victim
     * @return the list of types for which this keyword was registered
     */
    public EnumSet<NodeType> unregisterValidator(final String keyword)
    {
        final EnumSet<NodeType> ret = EnumSet.noneOf(NodeType.class);

        for (final NodeType type: NodeType.values()) {
            if (ignoredKeywords.get(type).remove(keyword))
                ret.add(type);
            if (validators.get(type).remove(keyword) != null)
                ret.add(type);
        }

        return ret;
    }

    /**
     * Get a validator set for a given context and instance
     *
     * @param context the context
     * @param instance the instance to be validated
     * @return the matching set of validators
     */
    public Collection<Validator> getValidators(final ValidationContext context,
        final JsonNode instance)
    {
        final JsonNode schemaNode = context.getSchema();
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
}
