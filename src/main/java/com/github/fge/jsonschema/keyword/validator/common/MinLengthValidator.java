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

package com.github.fge.jsonschema.keyword.validator.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.validator.helpers.PositiveIntegerValidator;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class MinLengthValidator
    extends PositiveIntegerValidator
{
    public MinLengthValidator(final JsonNode digested)
    {
        super("minLength", digested);
    }
    @Override
    public void validate(final Processor<FullData, FullData> processor,
        final ProcessingReport report, final FullData data)
        throws ProcessingException
    {
        final int size = data.getInstance().getNode().textValue()
            .length();

        if (size < intValue)
            report.error(newMsg(data).message(STRING_TOO_SHORT)
                .put(keyword, intValue).put("found", size));
    }
}
