/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.keyword.digest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.processors.digest.SchemaDigester;
import com.github.fge.jsonschema.processors.validation.ArraySchemaDigester;
import com.github.fge.jsonschema.processors.validation.ObjectSchemaDigester;

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
     * @return a set of {@link NodeType}s
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
