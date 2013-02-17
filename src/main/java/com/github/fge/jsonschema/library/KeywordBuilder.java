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
import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;
import com.github.fge.jsonschema.keyword.digest.helpers.IdentityDigester;
import com.github.fge.jsonschema.keyword.digest.helpers.SimpleDigester;
import com.github.fge.jsonschema.keyword.syntax.SyntaxChecker;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.Thawed;

import java.lang.reflect.Constructor;

import static com.github.fge.jsonschema.messages.ValidationConfigurationMessages.*;

public final class KeywordBuilder
    implements Thawed<Keyword>
{
    final String name;
    SyntaxChecker syntaxChecker;
    Digester digester;
    Constructor<? extends KeywordValidator> constructor;

    KeywordBuilder(final String name)
    {
        if (name == null)
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(NULL_NAME));
        this.name = name;
    }

    KeywordBuilder(final Keyword keyword)
    {
        name = keyword.name;
        syntaxChecker = keyword.syntaxChecker;
        digester = keyword.digester;
        constructor = keyword.constructor;
    }

    KeywordBuilder withSyntaxChecker(final SyntaxChecker syntaxChecker)
    {
        if (syntaxChecker == null)
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(NULL_SYNTAX_CHECKER));
        this.syntaxChecker = syntaxChecker;
        return this;
    }

    KeywordBuilder withDigester(final Digester digester)
    {
        if (digester == null)
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(NULL_DIGESTER));
        this.digester = digester;
        return this;
    }

    KeywordBuilder withIdentityDigester(final NodeType first,
        final NodeType... other)
    {
        digester = new IdentityDigester(name, checkType(first),
            checkTypes(other));
        return this;
    }

    KeywordBuilder withSimpleDigester(final NodeType first,
        final NodeType... other)
    {
        digester = new SimpleDigester(name, checkType(first),
            checkTypes(other));
        return this;
    }

    KeywordBuilder withValidatorClass(final Class<? extends KeywordValidator> c)
    {
        constructor = getConstructor(c);
        return this;
    }

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
                .message(NO_APPROPRIATE_CONSTRUCTOR));
        }
    }

    private static NodeType checkType(final NodeType type)
    {
        if (type == null)
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(NULL_TYPE));
        return type;
    }

    private static NodeType[] checkTypes(final NodeType... types)
    {
        for (final NodeType type: types)
            if (type == null)
                throw new ValidationConfigurationError(new ProcessingMessage()
                    .message(NULL_TYPE));
        return types;
    }
}
