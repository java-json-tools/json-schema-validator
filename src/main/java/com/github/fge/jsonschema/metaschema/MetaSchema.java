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

package com.github.fge.jsonschema.metaschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.Keyword;
import com.github.fge.jsonschema.old.keyword.KeywordValidator;
import com.github.fge.jsonschema.old.syntax.SyntaxChecker;
import com.github.fge.jsonschema.ref.JsonRef;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.Map;

/**
 * Metaschema main class
 *
 * <p>This class centralizes all elements necessary to define a working
 * metaschema. Its elements are:</p>
 *
 * <ul>
 *     <li>the URI of that metaschema (ie, what schemas written against that
 *     metaschema refer to using {@code $schema};</li>
 *     <li>(optionally) a raw JSON document representing that metaschema;</li>
 *     <li>a set of keywords and format attributes.</li>
 * </ul>
 *
 * <p>This is the base for all metaschema customizations you might want to do,
 * such as adding custom keywords and format attributes. You can either build
 * a completely new metaschema (using {@link #builder()} or base yourself on an
 * already existing metaschema (using {@link #basedOn(BuiltinSchemas)}).</p>
 *
 * @see JsonSchemaFactory.Builder#addMetaSchema(MetaSchema, boolean)
 * @see BuiltinSchemas
 */
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

    /**
     * Return a new builder for a totally empty metaschema
     *
     * @return a {@link Builder}
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Return a new builder based on an existing, builtin metaschema
     *
     * @param builtin the builtin metaschema used as a base
     * @return a {@link Builder}
     */
    public static Builder basedOn(final BuiltinSchemas builtin)
    {
        return new Builder(builtin);
    }

    /**
     * Return a complete copy of a builtin metaschema
     *
     * @param builtin the builtin metaschema
     * @return a ready-to-use metaschema
     */
    public static MetaSchema copyOf(final BuiltinSchemas builtin)
    {
        return new Builder(builtin).build();
    }

    /**
     * Return the URI of that metaschema
     *
     * @return the URI as a {@link JsonRef}
     */
    public JsonRef getDollarSchema()
    {
        return dollarSchema;
    }

    /**
     * Return the list of keyword validators for that metaschema
     *
     * @return an immutable map
     */
    public Map<String, Class<? extends KeywordValidator>> getValidators()
    {
        return validators;
    }

    /**
     * Return the list of syntax checkers for that metaschema
     *
     * @return an immutable map
     */
    public Map<String, SyntaxChecker> getSyntaxCheckers()
    {
        return syntaxCheckers;
    }

    /**
     * Return the list of format attributes for that metaschema
     *
     * @return an immutable map
     */
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
            dollarSchema = JsonRef.fromURI(builtin.getURI());
            rawSchema = builtin.getRawSchema();
            syntaxCheckers = Maps.newHashMap(builtin.checkers);
            validators = Maps.newHashMap(builtin.validators);
            formatAttributes = Maps.newHashMap(builtin.formatAttributes);
        }

        /**
         * Set a new URI for the metaschema
         *
         * @param uri the URI as a string
         * @return this
         * @throws IllegalArgumentException provided string is not a URI
         */
        public Builder withURI(final String uri)
        {
            Preconditions.checkNotNull(uri, "URI cannot be null");
            dollarSchema = JsonRef.fromURI(URI.create(uri));
            return this;
        }

        /**
         * Assign a JSON representation of that metaschema
         *
         * <p>Note: correctness of the representation is not checked</p>
         *
         * @param rawSchema the JSON
         * @return this
         */
        public Builder withRawSchema(final JsonNode rawSchema)
        {
            this.rawSchema = rawSchema;
            return this;
        }

        /**
         * Add a new keyword to that metaschema
         *
         * <p>Note: if a keyword by the same name already existed, this method
         * overrides it completely without warning.</p>
         *
         * @param keyword the new keyword
         * @return this
         * @throws NullPointerException keyword is null
         */
        public Builder addKeyword(final Keyword keyword)
        {
            Preconditions.checkNotNull(keyword, "keyword must not be null");
            final String name = keyword.getName();
            final SyntaxChecker checker = keyword.getSyntaxChecker();
            final Class<? extends KeywordValidator> validator
                = keyword.getValidatorClass();

            syntaxCheckers.remove(name);
            validators.remove(name);

            if (checker != null)
                syntaxCheckers.put(name, checker);
            if (validator != null)
                validators.put(name, validator);

            return this;
        }

        /**
         * Remove a keyword from that metaschema
         *
         * <p>If the keyword did not previously exist, this operation has no
         * effect.</p>
         *
         * @param name the name of the keyword to rename
         * @return this
         * @throws NullPointerException name is null
         */
        public Builder removeKeyword(final String name)
        {
            Preconditions.checkNotNull(name, "name must not be null");
            syntaxCheckers.remove(name);
            validators.remove(name);
            return this;
        }

        /**
         * Add a format attribute to the metaschema
         *
         * <p>If a format attribute by that name already existed, this method
         * overrides the previous attribute without warning.</p>
         *
         * @param fmt the name of the attribute
         * @param formatAttribute the implementation of the attribute
         * @return this
         * @throws NullPointerException the attribute name, or implementation,
         * are null
         */
        public Builder addFormatAttribute(final String fmt,
            final FormatAttribute formatAttribute)
        {
            Preconditions.checkNotNull(fmt, "format attribute name must not " +
                "be null");
            Preconditions.checkNotNull(formatAttribute,
                "format attribute implementation must not be null");
            formatAttributes.put(fmt, formatAttribute);
            return this;
        }

        /**
         * Remove a format attribute from this metaschema
         *
         * <p>If the format attribute did not exist previously, this method has
         * no effect.</p>
         *
         * @param fmt the attribute name to remove
         * @return this
         * @throws NullPointerException the attribute name is null
         */
        public Builder removeFormatAttribute(final String fmt)
        {
            Preconditions.checkNotNull(fmt, "format attribute name must not " +
                "be null");
            formatAttributes.remove(fmt);
            return this;
        }

        /**
         * Add a keyword registry to that metaschema
         *
         * @param registry the registry
         * @return this
         * @deprecated This is {@code public} only because {@link
         * JsonSchemaFactory} needs it
         */
        @Deprecated
        public Builder addKeywordRegistry(final KeywordRegistry registry)
        {
            syntaxCheckers.putAll(registry.getSyntaxCheckers());
            validators.putAll(registry.getValidators());
            formatAttributes.putAll(registry.getFormatAttributes());
            return this;
        }

        /**
         * Build the metaschema
         *
         * @return a metaschema
         * @throws NullPointerException no URI has been provided for that
         * metaschema
         * @throws IllegalArgumentException provided URI for that metaschema
         * is not an absolute JSON Reference
         */
        public MetaSchema build()
        {
            return new MetaSchema(this);
        }
    }
}
