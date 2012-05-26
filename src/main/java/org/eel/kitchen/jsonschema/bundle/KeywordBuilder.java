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

import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;

public final class KeywordBuilder
{
    private final String keyword;
    private SyntaxChecker syntaxChecker;

    private KeywordBuilder(final String keyword)
    {
        this.keyword = keyword;
    }

    public static KeywordBuilder forKeyword(final String keyword)
    {
        return new KeywordBuilder(keyword);
    }

    public KeywordBuilder withSyntaxChecker(final SyntaxChecker syntaxChecker)
    {
        this.syntaxChecker = syntaxChecker;
        return this;
    }

    public Keyword build()
    {
        return new Keyword(keyword, syntaxChecker);
    }
}
