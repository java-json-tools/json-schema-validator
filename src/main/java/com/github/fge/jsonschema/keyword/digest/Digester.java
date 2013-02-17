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

package com.github.fge.jsonschema.keyword.digest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.processors.digest.SchemaDigester;
import com.github.fge.jsonschema.processors.validation.ArraySchemaDigester;
import com.github.fge.jsonschema.processors.validation.ObjectSchemaDigester;
import com.github.fge.jsonschema.util.NodeType;

import java.util.EnumSet;

/**
 * Interface for a digester
 *
 * <p>A digester, as its name implies, digests a schema (which comes here as a
 * {@link JsonNode}) and returns a simplified form of it, according to its
 * context.</p>
 *
 * <p>It is mainly used for keywords, for building a simplified form of a schema
 * in order to ease the job of keyword construction; but most importantly, it
 * also reports the instance types supported by this keyword.</p>
 *
 * <p>It is also used to build a digested form of schemas for array/object
 * schema selections.</p>
 *
 * @see SchemaDigester
 * @see KeywordValidator
 * @see ArraySchemaDigester
 * @see ObjectSchemaDigester
 */
public interface Digester
{
    /**
     * Return the instance types handled by this digested form
     *
     * @return a set of {@link com.github.fge.jsonschema.util.NodeType}
     */
    EnumSet<NodeType> supportedTypes();

    /**
     * Digest a schema into a simplified form
     *
     * @param schema the schema to digest
     * @return the digested form
     */
    JsonNode digest(final JsonNode schema);
}
