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

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.Thawed;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.digest.helpers.IdentityDigester;
import com.github.fge.jsonschema.keyword.digest.helpers.SimpleDigester;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.serviceloader.MessageBundleFactory;

import java.lang.reflect.Constructor;

/**
 * A keyword builder -- the thawed version of a {@link Keyword}
 *
 * <p>Note that you may only supply a {@link SyntaxChecker} for a keyword, but
 * if you supply a validator class, the digester <b>must</b> also be present.
 * </p>
 */
public final class KeywordBuilder
    implements Thawed<Keyword>
{
    private static final MessageBundle BUNDLE
        = MessageBundleFactory.getBundle(JsonSchemaConfigurationBundle.class);
    final String name;
    SyntaxChecker syntaxChecker;
    Digester digester;
    Constructor<? extends KeywordValidator> constructor;

    /**
     * Create a new, empty keyword builder
     *
     * @param name the name of this keyword
     * @throws NullPointerException name is null
     * @see Keyword#newBuilder(String)
     */
    KeywordBuilder(final String name)
    {
        BUNDLE.checkNotNull(name, "nullName");
        this.name = name;
    }

    /**
     * Create a thawed version of a frozen keyword
     *
     * @param keyword the keyword
     * @see Keyword#thaw()
     */
    KeywordBuilder(final Keyword keyword)
    {
        name = keyword.name;
        syntaxChecker = keyword.syntaxChecker;
        digester = keyword.digester;
        constructor = keyword.constructor;
    }

    /**
     * Add a syntax checker to this builder
     *
     * @param syntaxChecker the syntax checker
     * @return this
     * @throws NullPointerException syntax checker is null
     */
    public KeywordBuilder withSyntaxChecker(final SyntaxChecker syntaxChecker)
    {
        BUNDLE.checkNotNull(syntaxChecker, "nullSyntaxChecker");
        this.syntaxChecker = syntaxChecker;
        return this;
    }

    /**
     * Add a digester to this builder
     *
     * @param digester the digester
     * @return this
     * @throws NullPointerException digester is null
     */
    public KeywordBuilder withDigester(final Digester digester)
    {
        BUNDLE.checkNotNull(digester, "nullDigester");
        this.digester = digester;
        return this;
    }

    /**
     * Set this keyword's digester to be an {@link IdentityDigester}
     *
     * @param first the first instance type supported by this keyword
     * @param other other instance types supported by this keyword
     * @return this
     * @throws NullPointerException one or more type(s) are null
     */
    public KeywordBuilder withIdentityDigester(final NodeType first,
        final NodeType... other)
    {
        digester = new IdentityDigester(name, checkType(first),
            checkTypes(other));
        return this;
    }

    /**
     * Set this keyword's digester to be a {@link SimpleDigester}
     *
     * @param first the first instance type supported by this keyword
     * @param other other instance types supported by this keyword
     * @return this
     * @throws NullPointerException one or more type(s) are null
     */
    public KeywordBuilder withSimpleDigester(final NodeType first,
        final NodeType... other)
    {
        digester = new SimpleDigester(name, checkType(first),
            checkTypes(other));
        return this;
    }

    /**
     * Set the validator class for this keyword
     *
     * @param c the class
     * @return this
     * @throws NullPointerException class is null
     * @throws ValidationConfigurationError failed to find an appropriate
     * constructor
     */
    public KeywordBuilder withValidatorClass(
        final Class<? extends KeywordValidator> c)
    {
        constructor = getConstructor(c);
        return this;
    }

    /**
     * Build a frozen version of this builder
     *
     * @return a {@link Keyword}
     * @see Keyword#Keyword(KeywordBuilder)
     */
    @Override
    public Keyword freeze()
    {
        return new Keyword(this);
    }


    private static Constructor<? extends KeywordValidator> getConstructor(
        final Class<? extends KeywordValidator> c)
    {
        try {
            return c.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException ignored) {
            throw new ValidationConfigurationError(new ProcessingMessage()
                .setMessage(BUNDLE.getMessage("noAppropriateConstructor")));
        }
    }

    private static NodeType checkType(final NodeType type)
    {
        BUNDLE.checkNotNull(type, "nullType");
        return type;
    }

    private static NodeType[] checkTypes(final NodeType... types)
    {
        for (final NodeType type: types)
            BUNDLE.checkNotNull(type, "nullType");
        return types;
    }
}
