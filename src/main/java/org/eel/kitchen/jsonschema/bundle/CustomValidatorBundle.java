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

package org.eel.kitchen.jsonschema.bundle;

import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;

import static org.eel.kitchen.util.NodeType.*;

/**
 * A custom validator bundle
 *
 * <p>This validator takes an existing bundle as an argument and allows you
 * to change the set of validated keywords and the validators to use.
 * </p>
 */
public final class CustomValidatorBundle
    extends AbstractValidatorBundle
{
    /**
     * Constructor
     *
     * @param bundle the validator bundle to use
     */
    public CustomValidatorBundle(final ValidatorBundle bundle)
    {
        svMap.putAll(bundle.syntaxValidators());
        kvMap.putAll(bundle.keywordValidators());
    }

    @Override
    public void registerValidator(final String keyword,
        final SyntaxValidator sv, final KeywordValidator kv,
        final NodeType... types)
    {
        if (types.length == 0)
            throw new IllegalArgumentException("cannot register a new keyword"
                + " with no JSON type to match against");

        if (keyword == null)
            throw new IllegalArgumentException("keyword is null");

        /*
         * We must check for syntax validators to determine whether a keyword
         * is registered: it is not mandatory to have a keyword validator.
         */
        if (svMap.containsKey(keyword))
            throw new IllegalArgumentException("keyword already registered");

        if (sv != null)
            svMap.put(keyword, sv);

        if (kv != null)
            registerKV(keyword, kv, types);
    }

    @Override
    public void unregisterValidator(final String keyword)
    {
        if (keyword == null)
            throw new IllegalArgumentException("keyword is null");

        /*
         * We choose to completely ignore keywords which were not registered
         * at this point
         */
        svMap.remove(keyword);

        for (final NodeType type: values())
            kvMap.get(type).remove(keyword);
    }
}
