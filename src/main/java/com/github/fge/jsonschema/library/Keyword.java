/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.library;

import com.github.fge.Frozen;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;

import java.lang.reflect.Constructor;


/**
 * Frozen keyword
 *
 * @see KeywordBuilder
 */
public final class Keyword
    implements Frozen<KeywordBuilder>
{
    /**
     * Name of this keyword
     */
    final String name;

    /**
     * Syntax checker
     */
    final SyntaxChecker syntaxChecker;

    /**
     * Digester
     */
    final Digester digester;

    /**
     * {@link KeywordValidator} constructor
     */
    final Constructor<? extends KeywordValidator> constructor;

    /**
     * Instantiate a new keyword builder
     *
     * @param name the name for this keyword
     * @return a new {@link KeywordBuilder}
     * @throws NullPointerException provided name is null
     * @see KeywordBuilder#KeywordBuilder(String)
     */
    public static KeywordBuilder newBuilder(final String name)
    {
        return new KeywordBuilder(name);
    }

    /**
     * Build a frozen keyword out of a thawed one
     *
     * @param builder the keyword builder to build from
     * @see KeywordBuilder#freeze()
     */
    Keyword(final KeywordBuilder builder)
    {
        name = builder.name;
        syntaxChecker = builder.syntaxChecker;
        digester = builder.digester;
        constructor = builder.constructor;
    }

    /**
     * Create a thawed version of this keyword
     *
     * @return a {@link KeywordBuilder}
     * @see KeywordBuilder#KeywordBuilder(Keyword)
     */
    @Override
    public KeywordBuilder thaw()
    {
        return new KeywordBuilder(this);
    }
}
