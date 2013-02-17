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

import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;
import com.github.fge.jsonschema.keyword.syntax.SyntaxChecker;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.util.Frozen;

import java.lang.reflect.Constructor;

import static com.github.fge.jsonschema.messages.ValidationConfigurationMessages.*;

public final class Keyword
    implements Frozen<KeywordBuilder>
{
    final String name;
    final SyntaxChecker syntaxChecker;
    final Digester digester;
    final Constructor<? extends KeywordValidator> constructor;

    public static KeywordBuilder newKeyword(final String name)
    {
        return new KeywordBuilder(name);
    }

    Keyword(final KeywordBuilder builder)
    {
        name = builder.name;
        syntaxChecker = builder.syntaxChecker;
        if (syntaxChecker == null)
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(NO_CHECKER));
        digester = builder.digester;
        constructor = builder.constructor;
    }

    @Override
    public KeywordBuilder thaw()
    {
        return new KeywordBuilder(this);
    }
}
