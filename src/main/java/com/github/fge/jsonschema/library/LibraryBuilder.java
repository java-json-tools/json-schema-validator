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

import com.github.fge.Thawed;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.serviceloader.MessageBundleFactory;

import java.lang.reflect.Constructor;

/**
 * Mutable version of a library
 *
 * <p>This is what you will use when you wish to create your own metaschema and
 * add either new keywords or format attributes to it.</p>
 *
 * <p><b>Important note:</b> if you add a keyword which already existed in this
 * builder, all traces of its previous definition, if any, will be
 * <b>removed</b>.</p>
 */
public final class LibraryBuilder
    implements Thawed<Library>
{
    private static final MessageBundle BUNDLE
        = MessageBundleFactory.getBundle(JsonSchemaConfigurationBundle.class);
    /**
     * Dictionary builder of syntax checkers
     */
    final DictionaryBuilder<SyntaxChecker> syntaxCheckers;

    /**
     * Dictionary builder of digesters
     */
    final DictionaryBuilder<Digester> digesters;

    /**
     * Dictionary builder of keyword validator constructors
     */
    final DictionaryBuilder<Constructor<? extends KeywordValidator>> validators;

    /**
     * Dictionary builder of format attributes
     */
    final DictionaryBuilder<FormatAttribute> formatAttributes;

    /**
     * No-arg constructor providing an empty library builder
     */
    LibraryBuilder()
    {
        syntaxCheckers = Dictionary.newBuilder();
        digesters = Dictionary.newBuilder();
        validators = Dictionary.newBuilder();
        formatAttributes = Dictionary.newBuilder();
    }

    /**
     * Constructor from an already existing library
     *
     * @param library the library
     * @see Library#thaw()
     */
    LibraryBuilder(final Library library)
    {
        syntaxCheckers = library.syntaxCheckers.thaw();
        digesters = library.digesters.thaw();
        validators = library.validators.thaw();
        formatAttributes = library.formatAttributes.thaw();
    }

    /**
     * Add a new keyword to this library
     *
     * @param keyword the keyword
     * @return this
     * @throws NullPointerException keyword is null
     */
    public LibraryBuilder addKeyword(final Keyword keyword)
    {
        BUNDLE.checkNotNull(keyword, "nullKeyword");
        final String name = keyword.name;
        removeKeyword(name);

        syntaxCheckers.addEntry(name, keyword.syntaxChecker);

        if (keyword.constructor != null) {
            digesters.addEntry(name, keyword.digester);
            validators.addEntry(name, keyword.constructor);
        }
        return this;
    }

    /**
     * Remove a keyword by its name
     *
     * @param name the name
     * @return this
     * @throws NullPointerException name is null
     */
    public LibraryBuilder removeKeyword(final String name)
    {
        BUNDLE.checkNotNull(name, "nullName");
        syntaxCheckers.removeEntry(name);
        digesters.removeEntry(name);
        validators.removeEntry(name);
        return this;
    }

    /**
     * Add a format attribute
     *
     * @param name the name for this attribute
     * @param attribute the format attribute
     * @return this
     * @throws NullPointerException the name or attribute is null
     */
    public LibraryBuilder addFormatAttribute(final String name,
        final FormatAttribute attribute)
    {
        removeFormatAttribute(name);
        BUNDLE.checkNotNull(attribute, "nullAttribute");
        formatAttributes.addEntry(name, attribute);
        return this;
    }

    /**
     * Remove a format attribute by its name
     *
     * @param name the format attribute name
     * @return this
     * @throws NullPointerException name is null
     */
    public LibraryBuilder removeFormatAttribute(final String name)
    {
        BUNDLE.checkNotNull(name, "nullFormat");
        formatAttributes.removeEntry(name);
        return this;
    }

    /**
     * Return a frozen version of this builder
     *
     * @return a new {@link Library}
     * @see Library#Library(LibraryBuilder)
     */
    @Override
    public Library freeze()
    {
        return new Library(this);
    }
}
