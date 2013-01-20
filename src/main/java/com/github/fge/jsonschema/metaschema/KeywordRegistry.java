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

package com.github.fge.jsonschema.metaschema;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.keyword.KeywordValidator;
import com.github.fge.jsonschema.main.Keyword;
import com.github.fge.jsonschema.syntax.SyntaxChecker;

import java.util.Map;

/**
 * Class holder for schema keywords and format attributes
 *
 * <p>Do not use this class anymore: use {@link MetaSchema} instead</p>
 *
 * @see KeywordRegistries
 */

// TODO: make package private in next version
public final class KeywordRegistry
{
    private final Map<String, SyntaxChecker> syntaxCheckers;

    private final Map<String, Class<? extends KeywordValidator>> validators;

    private final Map<String, FormatAttribute> formatAttributes;

    /**
     * Default constructor
     *
     * <p>By default, a keyword registry is completely empty.</p>
     */
    public KeywordRegistry()
    {
        syntaxCheckers = Maps.newHashMap();
        validators = Maps.newHashMap();
        formatAttributes = Maps.newHashMap();
    }

    /**
     * Add a set of syntax checkers (package private)
     *
     * @param map the syntax checker map
     */
    void addSyntaxCheckers(final Map<String, SyntaxChecker> map)
    {
        syntaxCheckers.putAll(map);
    }

    /**
     * Return an immutable copy of this registry's syntax checkers
     *
     * @return a map pairing keyword names and associated syntax checkers
     */
    public Map<String, SyntaxChecker> getSyntaxCheckers()
    {
        return ImmutableMap.copyOf(syntaxCheckers);
    }

    /**
     * Add a set of keyword validators (package private)
     *
     * @param map a map pairing keyword names and keyword validator classes
     */
    void addValidators(final Map<String, Class<? extends KeywordValidator>> map)
    {
        validators.putAll(map);
    }

    /**
     * Return an immutable copy of this registry's keyword validator classes
     *
     * @return a map pairing keyword names and associated keyword validator
     * classes
     */
    public Map<String, Class<? extends KeywordValidator>> getValidators()
    {
        return ImmutableMap.copyOf(validators);
    }

    /**
     * Add a keyword to this registry
     *
     * <p>Note: this method removes any previous traces of a keyword by the same
     * name.</p>
     *
     * @param keyword the keyword
     */
    public void addKeyword(final Keyword keyword)
    {
        Preconditions.checkNotNull(keyword, "keyword must not be null");

        final String name = keyword.getName();
        removeKeyword(name);

        final SyntaxChecker checker = keyword.getSyntaxChecker();
        if (checker != null)
            syntaxCheckers.put(name, checker);

        final Class<? extends KeywordValidator> validator
            = keyword.getValidatorClass();
        if (validator != null)
            validators.put(name, validator);
    }

    /**
     * Remove a keyword by its name
     *
     * @param name the keyword name
     */
    public void removeKeyword(final String name)
    {
        Preconditions.checkNotNull(name, "name must not be null");
        syntaxCheckers.remove(name);
        validators.remove(name);
    }

    /**
     * Add a set of format attributes (package private)
     *
     * @param map a map pairing the format attributes and their implementations
     */
    void addFormatAttributes(final Map<String, FormatAttribute> map)
    {
        formatAttributes.putAll(map);
    }

    /**
     * Add a format attribute
     *
     * @param name the attribute name
     * @param formatAttribute the attribute implementation
     */
    public void addFormatAttribute(final String name,
        final FormatAttribute formatAttribute)
    {
        Preconditions.checkNotNull(name, "name must not be null");
        Preconditions.checkNotNull(formatAttribute,
            "format attribute must not be null");
        formatAttributes.put(name, formatAttribute);
    }

    /**
     * Remove a format attribute by name
     *
     * @param name the name of the format attribute
     */
    public void removeFormatAttribute(final String name)
    {
        Preconditions.checkNotNull(name, "name must not be null");
        formatAttributes.remove(name);
    }

    /**
     * Return an immutable map of format attributes for this registry
     *
     * @return a map pairing format attribute names and their implementations
     */
    public Map<String, FormatAttribute> getFormatAttributes()
    {
        return ImmutableMap.copyOf(formatAttributes);
    }
}
