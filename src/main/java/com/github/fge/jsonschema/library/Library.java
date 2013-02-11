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

import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.util.Digester;
import com.github.fge.jsonschema.keyword.syntax.SyntaxChecker;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.util.Frozen;

import java.lang.reflect.Constructor;

public final class Library
    implements Frozen<LibraryBuilder>
{
    final Dictionary<SyntaxChecker> syntaxCheckers;
    final Dictionary<Digester> digesters;
    final Dictionary<Constructor<? extends KeywordValidator>> validators;
    final Dictionary<FormatAttribute> formatAttributes;

    public static LibraryBuilder newBuilder()
    {
        return new LibraryBuilder();
    }

    Library(final LibraryBuilder builder)
    {
        syntaxCheckers = builder.syntaxCheckers.freeze();
        digesters = builder.digesters.freeze();
        validators = builder.validators.freeze();
        formatAttributes = builder.formatAttributes.freeze();
    }

    Library(final Dictionary<SyntaxChecker> syntaxCheckers,
        final Dictionary<Digester> digesters,
        final Dictionary<Constructor<? extends KeywordValidator>> validators,
        final Dictionary<FormatAttribute> formatAttributes)
    {
        this.syntaxCheckers = syntaxCheckers;
        this.digesters = digesters;
        this.validators = validators;
        this.formatAttributes = formatAttributes;
    }

    public Dictionary<SyntaxChecker> getSyntaxCheckers()
    {
        return syntaxCheckers;
    }

    public Dictionary<Digester> getDigesters()
    {
        return digesters;
    }

    public Dictionary<Constructor<? extends KeywordValidator>> getValidators()
    {
        return validators;
    }

    public Dictionary<FormatAttribute> getFormatAttributes()
    {
        return formatAttributes;
    }

    @Override
    public LibraryBuilder thaw()
    {
        return new LibraryBuilder(this);
    }
}
