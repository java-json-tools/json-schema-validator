/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.library;

import com.github.fge.Frozen;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfigurationBuilder;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;

import java.lang.reflect.Constructor;

/**
 * A schema keyword library
 *
 * <p>A library contains all keywords defined for a schema, but also all format
 * attributes.</p>
 *
 * @see ValidationConfigurationBuilder#addLibrary(String, Library)
 * @see ValidationConfigurationBuilder#setDefaultLibrary(String, Library)
 * @see ValidationConfigurationBuilder#setDefaultVersion(SchemaVersion)
 */
public final class Library
    implements Frozen<LibraryBuilder>
{
    /**
     * Dictionary of syntax checkers
     */
    final Dictionary<SyntaxChecker> syntaxCheckers;

    /**
     * Dictionary of digesters
     */
    final Dictionary<Digester> digesters;

    /**
     * Dictionary of keyword validator constructors
     */
    final Dictionary<Constructor<? extends KeywordValidator>> validators;

    /**
     * Dictionary of format attributes
     */
    final Dictionary<FormatAttribute> formatAttributes;

    /**
     * Create a new, empty library builder
     *
     * @return a {@link LibraryBuilder}
     */
    public static LibraryBuilder newBuilder()
    {
        return new LibraryBuilder();
    }

    /**
     * Constructor from a library builder
     *
     * @param builder the builder
     * @see LibraryBuilder#freeze()
     */
    Library(final LibraryBuilder builder)
    {
        syntaxCheckers = builder.syntaxCheckers.freeze();
        digesters = builder.digesters.freeze();
        validators = builder.validators.freeze();
        formatAttributes = builder.formatAttributes.freeze();
    }

    /**
     * Internal constructor, do not use!
     *
     * @param syntaxCheckers map of syntax checkers
     * @param digesters map of digesters
     * @param validators map of keyword validator constructors
     * @param formatAttributes map of format attributes
     */
    Library(final Dictionary<SyntaxChecker> syntaxCheckers,
        final Dictionary<Digester> digesters,
        final Dictionary<Constructor<? extends KeywordValidator>> validators,
        final Dictionary<FormatAttribute> formatAttributes)
    {
        this.syntaxCheckers = syntaxCheckers;
        this.digesters = digesters;
        this.validators = validators;
        this.formatAttributes = formatAttributes;
    }

    /**
     * Get the dictionary of syntax checkers
     *
     * @return a dictionary
     */
    public Dictionary<SyntaxChecker> getSyntaxCheckers()
    {
        return syntaxCheckers;
    }

    /**
     * Get the dictionary of digesters
     *
     * @return a dictionary
     */
    public Dictionary<Digester> getDigesters()
    {
        return digesters;
    }

    /**
     * Get the dictionary of keyword validator constructors
     *
     * @return a dictionary
     */
    public Dictionary<Constructor<? extends KeywordValidator>> getValidators()
    {
        return validators;
    }

    /**
     * Get the dictionary of format attributes
     *
     * @return a dictionary
     */
    public Dictionary<FormatAttribute> getFormatAttributes()
    {
        return formatAttributes;
    }

    /**
     * Create a mutable version of this library
     *
     * @return a {@link LibraryBuilder}
     * @see LibraryBuilder#LibraryBuilder(Library)
     */
    @Override
    public LibraryBuilder thaw()
    {
        return new LibraryBuilder(this);
    }
}
