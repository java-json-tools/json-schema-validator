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

package com.github.fge.jsonschema.processing.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.NodeType;

import java.util.EnumSet;

public abstract class SyntaxChecker
{
    protected final String keyword;
    protected final EnumSet<NodeType> types;

    protected SyntaxChecker(final String keyword, final NodeType first,
        final NodeType... other)
    {
        this.keyword = keyword;
        types = EnumSet.of(first, other);
    }

    public final void checkSyntax(final SyntaxProcessor processor,
        final ProcessingReport report, final JsonSchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = tree.getCurrentNode().get(keyword);
        final NodeType type = NodeType.getNodeType(node);

        if (!types.contains(type)) {
            report.error(message("invalid primitive type for keyword")
                .put("allowed", types).put("found", type));
            return;
        }

        checkValue(processor, report, tree);
    }

    protected abstract void checkValue(final SyntaxProcessor processor,
        final ProcessingReport report, final JsonSchemaTree tree)
        throws ProcessingException;

    protected final ProcessingMessage message(final String msg)
    {
        return new ProcessingMessage().put("domain", "syntax")
            .put("keyword", keyword).msg(msg);
    }
}
