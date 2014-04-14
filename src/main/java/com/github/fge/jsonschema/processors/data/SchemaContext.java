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

package com.github.fge.jsonschema.processors.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.processors.digest.SchemaDigester;
import com.github.fge.jsonschema.processors.validation.ValidationChain;

/**
 * Input for both a {@link SchemaDigester} and a {@link ValidationChain}
 *
 * <p>This is essentially a {@link FullData} which only retains the type of the
 * instance to validate instead of the full instance.</p>
 *
 * @see NodeType#getNodeType(JsonNode)
 */
public final class SchemaContext
    implements MessageProvider
{
    private final SchemaTree schema;
    private final NodeType instanceType;

    public SchemaContext(final FullData data)
    {
        schema = data.getSchema();
        final JsonTree tree = data.getInstance();
        instanceType = tree != null
            ? NodeType.getNodeType(tree.getNode())
            : null;
    }

    public SchemaContext(final SchemaTree schema, final NodeType instanceType)
    {
        this.schema = schema;
        this.instanceType = instanceType;
    }

    public SchemaTree getSchema()
    {
        return schema;
    }

    public NodeType getInstanceType()
    {
        return instanceType;
    }

    @Override
    public ProcessingMessage newMessage()
    {
        return new ProcessingMessage().put("schema", schema);
    }
}
