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

package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;

public final class JsonSchemaContext
    extends ProcessingContext<ProcessingReport>
{
    private final ProcessingReport report = new ProcessingReport();

    private JsonSchemaTree schemaTree;
    private JsonTree tree;

    public JsonSchemaContext()
    {
        this(null, null);
    }

    public JsonSchemaContext(final JsonSchemaTree schemaTree)
    {
        this(schemaTree, null);
    }

    public JsonSchemaContext(final JsonSchemaTree schemaTree,
        final JsonTree tree)
    {
        setLogThreshold(LogThreshold.INFO);
        this.schemaTree = schemaTree;
        this.tree = tree;
    }

    public JsonSchemaTree getSchemaTree()
    {
        return schemaTree;
    }

    public void setSchemaTree(final JsonSchemaTree schemaTree)
    {
        this.schemaTree = schemaTree;
    }

    public JsonTree getTree()
    {
        return tree;
    }

    public void setTree(final JsonTree tree)
    {
        this.tree = tree;
    }

    @Override
    public void log(final ProcessingMessage msg)
    {
        report.addMessage(msg);
    }

    @Override
    public ProcessingException buildException(final ProcessingMessage msg)
    {
        return new ProcessingException(msg);
    }

    @Override
    public ProcessingMessage newMessage()
    {
        final ProcessingMessage ret = new ProcessingMessage();
        if (schemaTree != null)
            ret.put("schema", schemaTree);
        if (tree != null)
            ret.put("instance", tree);
        return ret;
    }

    @Override
    public ProcessingReport getOutput()
    {
        return report;
    }
}
