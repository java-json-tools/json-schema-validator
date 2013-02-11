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

package com.github.fge.jsonschema.keyword.syntax.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.syntax.AbstractSyntaxChecker;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.NodeType;

import java.util.Collection;

public abstract class SchemaOrSchemaArraySyntaxChecker
    extends AbstractSyntaxChecker
{
    private final JsonPointer basePointer;

    protected SchemaOrSchemaArraySyntaxChecker(final String keyword)
    {
        super(keyword, NodeType.ARRAY, NodeType.OBJECT);
        basePointer = JsonPointer.empty().append(keyword);
    }

    @Override
    protected final void checkValue(final Collection<JsonPointer> pointers,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        collectPointers(pointers, tree);
        extraChecks(report, tree);
    }

    protected abstract void extraChecks(final ProcessingReport report,
        final SchemaTree tree)
        throws ProcessingException;

    private void collectPointers(final Collection<JsonPointer> pointers,
        final SchemaTree tree)
    {
        final JsonNode node = getNode(tree);
        if (node.isObject()) {
            pointers.add(basePointer);
            return;
        }

        for (int index = 0; index < node.size(); index++)
            pointers.add(basePointer.append(index));
    }
}
