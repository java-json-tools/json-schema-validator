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

package com.github.fge.jsonschema.keyword.digest.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.digest.helpers.NumericDigester;

/**
 * Digester for {@code minimum}
 *
 * <p>This uses {@link NumericDigester} as a base, and also stores information
 * about the presence (or not) of {@code exclusiveMinimum}.</p>
 */
public final class MinimumDigester
    extends NumericDigester
{
    private static final Digester INSTANCE = new MinimumDigester();

    public static Digester getInstance()
    {
        return INSTANCE;
    }

    private MinimumDigester()
    {
        super("minimum");
    }
    @Override
    public JsonNode digest(final JsonNode schema)
    {
        final ObjectNode ret = digestedNumberNode(schema);
        ret.put("exclusive", schema.path("exclusiveMinimum").asBoolean(false));
        return ret;
    }
}
