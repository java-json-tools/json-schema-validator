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

package com.github.fge.jsonschema.keyword.validator.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;

import java.util.EnumSet;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class DraftV4TypeValidator
    extends AbstractKeywordValidator
{
    private final EnumSet<NodeType> types = EnumSet.noneOf(NodeType.class);

    public DraftV4TypeValidator(final JsonNode digest)
    {
        super("type");
        for (final JsonNode node: digest.get(keyword))
            types.add(NodeType.fromName(node.textValue()));
    }

    @Override
    public void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        final NodeType type
            = NodeType.getNodeType(data.getInstance().getCurrentNode());

        if (!types.contains(type))
            report.error(newMsg(data).msg(TYPE_NO_MATCH)
                .put("expected", types).put("found", type));
    }

    @Override
    public String toString()
    {
        return keyword + ": " + types;
    }
}
