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

package com.github.fge.jsonschema.processors.data;

import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.NodeType;

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

    public SchemaContext withSchema(final SchemaTree schemaTree)
    {
        return new SchemaContext(schemaTree, instanceType);
    }

    @Override
    public ProcessingMessage newMessage()
    {
        return new ProcessingMessage().put("schema", schema);
    }
}
