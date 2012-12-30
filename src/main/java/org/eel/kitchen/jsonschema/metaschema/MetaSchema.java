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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.eel.kitchen.jsonschema.format.FormatAttribute;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.Keyword;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.TypeOnlySyntaxChecker;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.net.URI;
import java.util.Map;

public final class MetaSchema
{
    private final JsonRef dollarSchema;
    private final JsonNode rawSchema;
    private final Map<String, SyntaxChecker> syntaxCheckers;
    private final Map<String, Class<? extends KeywordValidator>> validators;
    private final Map<String, FormatAttribute> formatAttributes;

    private MetaSchema(final Builder builder)
    {
        final JsonRef ref = builder.dollarSchema;
        Preconditions.checkNotNull(ref, "a schema URI must be provided");
        Preconditions.checkArgument(ref.isAbsolute(),
            "provided schema URI is not an absolute JSON Reference");

        dollarSchema = ref;
        rawSchema = builder.rawSchema;
        syntaxCheckers = ImmutableMap.copyOf(builder.syntaxCheckers);
        validators = ImmutableMap.copyOf(builder.validators);
        formatAttributes = ImmutableMap.copyOf(builder.formatAttributes);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static Builder basedOn(final BuiltinSchemas builtin)
    {
        return new Builder(builtin);
    }

    public Map<String, Class<? extends KeywordValidator>> getValidators()
    {
        return validators;
    }

    public Map<String, SyntaxChecker> getSyntaxCheckers()
    {
        return syntaxCheckers;
    }

    public Map<String, FormatAttribute> getFormatAttributes()
    {
        return formatAttributes;
    }

    public static final class Builder
    {
        private JsonRef dollarSchema;
        private JsonNode rawSchema;

        private final Map<String, SyntaxChecker> syntaxCheckers;
        private final Map<String, Class<? extends KeywordValidator>> validators;
        private final Map<String, FormatAttribute> formatAttributes;

        private Builder()
        {
            syntaxCheckers = Maps.newHashMap();
            validators = Maps.newHashMap();
            formatAttributes = Maps.newHashMap();
        }

        private Builder(final BuiltinSchemas builtin)
        {
            syntaxCheckers = Maps.newHashMap(builtin.checkers);
            validators = Maps.newHashMap(builtin.validators);
            formatAttributes = Maps.newHashMap(builtin.formatAttributes);
        }

        public Builder withURI(final String uri)
        {
            dollarSchema = JsonRef.fromURI(URI.create(uri));
            return this;
        }

        public Builder withRawSchema(final JsonNode rawSchema)
        {
            this.rawSchema = rawSchema;
            return this;
        }

        public Builder withNewKeyword(final Keyword keyword)
        {
            final String name = keyword.getName();
            final SyntaxChecker checker = keyword.getSyntaxChecker();
            final Class<? extends KeywordValidator> validator
                = keyword.getValidatorClass();

            if (checker != null)
                syntaxCheckers.put(name, checker);
            if (validator != null)
                validators.put(name, validator);

            return this;
        }

        public Builder withNewKeyword(final String name, final NodeType first,
            final NodeType... other)
        {
            Preconditions.checkNotNull(name, "name must not be null");
            syntaxCheckers.put(name, new TypeOnlySyntaxChecker(name,
                first, other));
            validators.remove(name);
            return this;
        }

        public Builder withoutKeyword(final String name)
        {
            Preconditions.checkNotNull(name, "name must not be null");
            syntaxCheckers.remove(name);
            validators.remove(name);
            return this;
        }

        public Builder withNewFormatAttribute(final String fmt,
            final FormatAttribute formatAttribute)
        {
            Preconditions.checkNotNull(fmt, "format attribute name must not " +
                "be null");
            Preconditions.checkNotNull(formatAttribute,
                "format attribute implementation must not be null");
            formatAttributes.put(fmt, formatAttribute);
            return this;
        }

        public Builder withoutFormatAttribute(final String fmt)
        {
            Preconditions.checkNotNull(fmt, "format attribute name must not " +
                "be null");
            formatAttributes.remove(fmt);
            return this;
        }

        public MetaSchema build()
        {
            return new MetaSchema(this);
        }
    }
}
