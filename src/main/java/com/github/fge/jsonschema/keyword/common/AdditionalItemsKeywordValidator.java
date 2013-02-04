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

package com.github.fge.jsonschema.keyword.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.AbstractKeywordValidator;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class AdditionalItemsKeywordValidator
    extends AbstractKeywordValidator
{
    private final boolean additionalOK;
    private final int itemsSize;

    public AdditionalItemsKeywordValidator(final JsonNode node)
    {
        super("additionalItems");
        final JsonNode items = node.path("items");

        if (!items.isArray()) {
            additionalOK = true;
            itemsSize = 0;
            return;
        }

        itemsSize = items.size();
        additionalOK = node.get(keyword).asBoolean(true);
    }
    @Override
    public void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        if (additionalOK)
            return;

        final int size = data.getInstance().getCurrentNode().size();
        if (size > itemsSize)
            report.error(newMsg(data).msg(ADDITIONAL_ITEMS_NOT_ALLOWED)
                .put("allowed", itemsSize).put("found", size));
    }

}
