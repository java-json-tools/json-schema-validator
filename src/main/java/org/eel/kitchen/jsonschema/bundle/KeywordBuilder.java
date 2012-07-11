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

import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;

/**
 * Class used to build a new keyword.
 *
 * <p>In order to register a new keyword, you need at the very least its
 * name. Other elements, such as its {@link SyntaxChecker} and even its
 * {@link KeywordValidator} class, are optional.</p>
 *
 * <p>Remember that, if you include a keyword validator,
 * then it <i>assumes</i> that the syntax is already correct.</p>
 *
 * Here is, for instance, the way to add a keyword for {@code description},
 * which plays no role in validation, but if present, must be a JSON text node:
 *
 * <pre>
 *  // Our SyntaxChecker
 *  public final class DescriptionSyntaxChecker
 *      extends SimpleSyntaxChecker
 *  {
 *      private static final SyntaxChecker instance
 *          = new DescriptionSyntaxChecker();
 *
 *      public static SyntaxChecker getInstance()
 *      {
 *          return instance;
 *      }
 *
 *      private DescriptionSyntaxChecker()
 *      {
 *          super("description", NodeType.STRING);
 *      }
 *  }
 *
 *  // Generating the keyword
 *  final Keyword description = KeywordBuilder.forKeyword("description")
 *      .withSyntaxChecker(DescriptionSyntaxChecker.getInstance())
 *      .build();
 * </pre>
 *
 * @see Keyword
 * @see SyntaxChecker
 * @see KeywordValidator
 */
public final class KeywordBuilder
{
    private final String keyword;
    private SyntaxChecker syntaxChecker;
    private Class<? extends KeywordValidator> validatorClass;

    private KeywordBuilder(final String keyword)
    {
        this.keyword = keyword;
    }

    /**
     * The one and only static factory method to build an instance
     *
     * @param keyword the keyword to use
     * @return the newly created instance
     */
    public static KeywordBuilder forKeyword(final String keyword)
    {
        return new KeywordBuilder(keyword);
    }

    /**
     * Add a syntax checker to this keyword
     *
     * @param syntaxChecker the syntax checker, already instantiated
     * @return this
     */
    public KeywordBuilder withSyntaxChecker(final SyntaxChecker syntaxChecker)
    {
        this.syntaxChecker = syntaxChecker;
        return this;
    }

    /**
     * Add the keyword validator class
     *
     * <p>We add the class, not an instance, since the generated object is
     * dependent on the schema being passed.</p>
     *
     * @param validatorClass the class
     * @return this
     */
    public KeywordBuilder withValidatorClass(
        final Class<? extends KeywordValidator> validatorClass)
    {
        this.validatorClass = validatorClass;
        return this;
    }

    /**
     * Build the {@link Keyword}
     *
     * @return the keyword
     */
    public Keyword build()
    {
        return new Keyword(keyword, syntaxChecker, validatorClass);
    }
}
