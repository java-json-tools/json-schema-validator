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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.factories;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
 * @see ValidatorFactory
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
     * Constructor
     *
     * @param bundle the validator bundle to use
     */
    public KeywordFactory(final ValidatorBundle bundle)
    {
        for (final NodeType type: NodeType.values()) {
            validators.put(type, new HashMap<String, KeywordValidator>());
            ignoredKeywords.put(type, new HashSet<String>());
        }

        pushValidators(bundle);
        pushIgnored(bundle);
    }

    /**
     * Fill the map of ignored keywords registered for this bundle
     *
     * @param bundle the validator bundle
     */
    private void pushIgnored(final ValidatorBundle bundle)
    {
        final Map<NodeType, Set<String>> bundleMap =
            bundle.ignoredKeywordValidators();

        for (final Map.Entry<NodeType, Set<String>> entry: bundleMap.entrySet())
            ignoredKeywords.get(entry.getKey()).addAll(entry.getValue());
    }

    /**
     * Fill the map of registered keyword validators for this bundle
     *
     * @param bundle the validator bundle
     */
    private void pushValidators(final ValidatorBundle bundle)
    {
        final Map<NodeType, Map<String, KeywordValidator>> bundleMap
            = bundle.keywordValidators();

        for (final Map.Entry<NodeType, Map<String, KeywordValidator>> entry:
            bundleMap.entrySet())
            validators.get(entry.getKey()).putAll(entry.getValue());
    }

    /**
     * Get a validator set for a given context and instance
     *
     * <p>If no keyword is found (which is possible if the instance is of a
     * type for which none of the current schema keywords apply),
     * then an {@link AlwaysTrueValidator} is returned.
     * </p>
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
