/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.library;

import com.github.fge.Thawed;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.validator.KeywordValidatorFactory;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

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
        = MessageBundles.getBundle(JsonSchemaConfigurationBundle.class);
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
    final DictionaryBuilder<KeywordValidatorFactory> validators;

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

        if (keyword.validatorFactory != null) {
            digesters.addEntry(name, keyword.digester);
            validators.addEntry(name, keyword.validatorFactory);
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
        BUNDLE.checkNotNullPrintf(attribute, "nullAttribute", name);
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
