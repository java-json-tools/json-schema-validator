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
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;

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
            dollarSchema = JsonRef.fromURI(builtin.getURI());
            rawSchema = builtin.getRawSchema();
            syntaxCheckers = Maps.newHashMap(builtin.checkers);
            validators = Maps.newHashMap(builtin.validators);
            formatAttributes = Maps.newHashMap(builtin.formatAttributes);
        }

        public MetaSchema build()
        {
            return new MetaSchema(this);
        }
    }
}
