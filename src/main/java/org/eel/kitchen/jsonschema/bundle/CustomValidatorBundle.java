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
 * A validator bundle
 *
 * <p>As validators vary from one schema to another (and as you can even
 * register your own validators), this class is here to relieve factories
 * from registering validators themselves.</p>
 */
public final class CustomValidatorBundle
    extends AbstractValidatorBundle
{
    public CustomValidatorBundle(final ValidatorBundle bundle)
    {
        svMap.putAll(bundle.syntaxValidators());
        ignoredSV.addAll(bundle.ignoredSyntaxValidators());
        kvMap.putAll(bundle.keywordValidators());
        ignoredKV.putAll(bundle.ignoredKeywordValidators());
    }


    @Override
    public void registerValidator(final String keyword,
        final SyntaxValidator sv, final KeywordValidator kv,
        final NodeType... types)
    {
        if (types.length == 0)
            throw new IllegalArgumentException("cannot register a new keyword"
                + " with no JSON type to match against");
        /*
         * We only need to check for syntax validators: the public
         * registration mechanism guarantees that the keyword set of syntax
         * and keyword validators is the same. As to the "private" API,
         * it is up to the developer to ensure this.
         */
        if (ignoredSV.contains(keyword) || svMap.containsKey(keyword))
            throw new IllegalArgumentException(keyword + " already registered");

        if (sv == null)
            ignoredSV.add(keyword);
        else
            svMap.put(keyword, sv);

        if (kv == null)
            registerIgnoredKV(keyword, types);
        else
            registerKV(keyword, kv, types);
    }

    @Override
    public void unregisterValidator(final String keyword)
    {
        /*
         * We choose to completely ignore keywords which were not registered
         * at this point
         */
        ignoredSV.remove(keyword);
        svMap.remove(keyword);

        for (final NodeType type: values()) {
            ignoredKV.get(type).remove(keyword);
            kvMap.get(type).remove(keyword);
        }
    }
}
