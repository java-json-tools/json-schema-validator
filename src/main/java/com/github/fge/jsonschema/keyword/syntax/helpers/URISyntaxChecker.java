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

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.keyword.syntax.AbstractSyntaxChecker;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.NodeType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import static com.github.fge.jsonschema.messages.SyntaxMessages.*;

/**
 * Helper class to validate the syntax of all keywords whose value must be a URI
 *
 * <p>Note that this checker checks that URIs are normalized.</p>
 */
public final class URISyntaxChecker
    extends AbstractSyntaxChecker
{
    public URISyntaxChecker(final String keyword)
    {
        super(keyword, NodeType.STRING);
    }

    @Override
    protected void checkValue(final Collection<JsonPointer> pointers,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        final String s = getNode(tree).textValue();

        try {
            final URI uri = new URI(s);
            if (!uri.equals(uri.normalize()))
                report.error(newMsg(tree, URI_NOT_NORMALIZED).put("value", s));
        } catch (URISyntaxException ignored) {
            report.error(newMsg(tree, INVALID_URI).put("value", s));
        }
    }
}
