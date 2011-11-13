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
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.keyword.format.CacheableValidator;
import org.eel.kitchen.jsonschema.keyword.format.FormatValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;

/**
 * Factory centralizing all validator factories, and in charge or returning
 * validators as well.
 */

public final class ValidatorFactory
{
    /**
     * The {@link KeywordValidator} factory
     */
    private final KeywordFactory keywordFactory;

    /**
     * The {@link SyntaxValidator} factory
     */
    private final SyntaxFactory syntaxFactory;

    /**
     * The {@link FormatValidator} factory
     */
    private final FormatFactory formatFactory;

    public ValidatorFactory()
    {
        keywordFactory = new KeywordFactory();
        syntaxFactory = new SyntaxFactory();
        formatFactory = new FormatFactory();
    }

    /**
     * Return a {@link SyntaxValidator} for the schema node located in a
     * {@link ValidationContext}
     *
     * @param context the context containing the schema node
     * @return the matching validator
     */
    public CacheableValidator getSyntaxValidator(final ValidationContext context)
    {
        return syntaxFactory.getValidator(context);
    }

    /**
     * Return a {@link KeywordValidator} to validate an instance against a
     * given schema
     *
     * @param context the context containing the schema
     * @param instance the instance to validate
     * @return the matching validator
     */
    public CacheableValidator getInstanceValidator(
        final ValidationContext context, final JsonNode instance)
    {
        return keywordFactory.getValidator(context, instance);
    }

    /**
     * Get a validator for a given format specification,
     * context and instance to validate
     *
     * @param context the context
     * @param fmt the format specification
     * @param instance the instance to validate
     * @return the matching {@link FormatValidator}
     */
    public FormatValidator getFormatValidator(final ValidationContext context,
        final String fmt, final JsonNode instance)
    {
        return formatFactory.getFormatValidator(context, fmt, instance);
    }

    /**
     * Register a validator for a new keyword
     *
     * <p>Note that if you wish to replace validators for an existing
     * keyword, then you <b>must</b> call
     * {@link #unregisterValidator(String)} first.</p>
     *
     * @param keyword the new/modified keyword
     * @param sv the {@link SyntaxValidator} implementation
     * @param kv the {@link KeywordValidator} implementation
     * @param types the list of JSON types the keyword validator is able to
     * validate
     *
     * @see SyntaxFactory#registerValidator(String, Class)
     * @see KeywordFactory#registerValidator(String, Class, NodeType...)
     */
    public void registerValidator(final String keyword,
        final Class<? extends SyntaxValidator> sv,
        final Class<? extends KeywordValidator> kv, final NodeType... types)
    {
        syntaxFactory.registerValidator(keyword, sv);
        keywordFactory.registerValidator(keyword, kv, types);
    }

    /**
     * Unregister all validators ({@link SyntaxValidator} and
     * {@link KeywordValidator}) for a given keyword. Note that the null case
     * is handled in the factories themselves.
     *
     * @param keyword the victim
     */
    public void unregisterValidator(final String keyword)
    {
        syntaxFactory.unregisterValidator(keyword);
        keywordFactory.unregisterValidator(keyword);
    }
}
