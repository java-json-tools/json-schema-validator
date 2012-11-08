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

package org.eel.kitchen.jsonschema.metaschema;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.eel.kitchen.jsonschema.format.FormatAttribute;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.Keyword;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;

import java.util.Map;

public final class KeywordRegistry
{
    private final Map<String, SyntaxChecker> syntaxCheckers;

    private final Map<String, Class<? extends KeywordValidator>> validators;

    private final Map<String, FormatAttribute> formatAttributes;

    public KeywordRegistry()
    {
        syntaxCheckers = Maps.newHashMap();
        validators = Maps.newHashMap();
        formatAttributes = Maps.newHashMap();
    }

    void addSyntaxCheckers(final Map<String, SyntaxChecker> map)
    {
        syntaxCheckers.putAll(map);
    }

    public Map<String, SyntaxChecker> getSyntaxCheckers()
    {
        return ImmutableMap.copyOf(syntaxCheckers);
    }

    void addValidators(final Map<String, Class<? extends KeywordValidator>> map)
    {
        validators.putAll(map);
    }

    public Map<String, Class<? extends KeywordValidator>> getValidators()
    {
        return ImmutableMap.copyOf(validators);
    }

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

    public void removeKeyword(final String name)
    {
        Preconditions.checkNotNull(name, "name must not be null");
        syntaxCheckers.remove(name);
        validators.remove(name);
    }

    public void addFormatAttributes(final Map<String, FormatAttribute> map)
    {
        formatAttributes.putAll(map);
    }

    public void addFormatAttribute(final String name,
        final FormatAttribute formatAttribute)
    {
        Preconditions.checkNotNull(name, "name must not be null");
        Preconditions.checkNotNull(formatAttribute,
            "format attribute must not be null");
        formatAttributes.put(name, formatAttribute);
    }

    public void removeFormatAttribute(final String name)
    {
        Preconditions.checkNotNull(name, "name must not be null");
        formatAttributes.remove(name);
    }

    public Map<String, FormatAttribute> getFormatAttributes()
    {
        return ImmutableMap.copyOf(formatAttributes);
    }
}
