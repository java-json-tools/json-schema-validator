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

package org.eel.kitchen.jsonschema.bundle;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;

import java.util.Map;

/**
 * A keyword bundle
 *
 * <p>You can either create a new, completely empty, keyword bundle,
 * or use one of the default bundles and extend it. For instance:</p>
 *
 * <pre>
 *     final KeywordBundle bundle = KeywordBundles.defaultBundle();
 *
 *     final Keyword k1 = ...;
 *     final Keyword k2 = ...;
 *
 *     bundle.registerKeyword(k1);
 *     bundle.registerKeyword(k2);
 * </pre>
 *
 * @see Keyword
 * @see KeywordBundles
 * @see JsonSchemaFactory
 */
public final class KeywordBundle
{
    private final Map<String, SyntaxChecker> syntaxCheckers = Maps.newHashMap();

    private final Map<String, Class<? extends KeywordValidator>> validators
        = Maps.newHashMap();

    /**
     * Package-private method to generate a full copy of this bundle
     *
     * @return an identical copy of this bundle
     */
    KeywordBundle copy()
    {
        final KeywordBundle ret = new KeywordBundle();
        ret.syntaxCheckers.putAll(syntaxCheckers);
        ret.validators.putAll(validators);
        return ret;
    }

    /**
     * Register a keyword for this bundle
     *
     * <p>Note that if a keyword by the same name already exists, its syntax
     * validator and keyword validator will be reset to the new values (and
     * unregistered if any of these is {@code null}).</p>
     *
     * @see Keyword.Builder
     * @see Keyword
     *
     * @param keyword the keyword to register
     */
    public void registerKeyword(final Keyword keyword)
    {
        final String name = keyword.getName();

        final SyntaxChecker checker = keyword.getSyntaxChecker();
        syntaxCheckers.remove(name);
        if (checker != null)
            syntaxCheckers.put(name, checker);

        final Class<? extends KeywordValidator> validatorClass
            = keyword.getValidatorClass();
        validators.remove(name);
        if (validatorClass != null)
            validators.put(name, validatorClass);
    }

    /**
     * Unregister a keyword
     *
     * @param name the name of the keyword to unregister
     */
    public void unregisterKeyword(final String name)
    {
        syntaxCheckers.remove(name);
        validators.remove(name);
    }

    /**
     * Merge with another keyword bundle
     *
     * <p>Note: this operation is potentially dangerous if you are not careful.
     * This method blindingly replaces the existing bundle's syntax checkers and
     * keyword validators with those of the other bundle. Use with care!</p>
     *
     * @param other the other bundle
     */
    public void mergeWith(final KeywordBundle other)
    {
        syntaxCheckers.putAll(other.syntaxCheckers);
        validators.putAll(other.validators);
    }

    /**
     * Get the list of syntax checkers for this bundle
     *
     * @return a map of syntax checkers
     */
    public Map<String, SyntaxChecker> getSyntaxCheckers()
    {
        return ImmutableMap.copyOf(syntaxCheckers);
    }

    /**
     * Get the list of keyword validators for this bundle
     *
     * @return a map of keyword validators
     */
    public Map<String, Class<? extends KeywordValidator>> getValidators()
    {
        return ImmutableMap.copyOf(validators);
    }
}
