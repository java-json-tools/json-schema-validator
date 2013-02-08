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
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.RhinoHelper;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class PatternValidator
    extends AbstractKeywordValidator
{
    public PatternValidator(final JsonNode digest)
    {
        super("pattern");
    }

    @Override
    public void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        final String regex = data.getSchema().getCurrentNode().get(keyword)
            .textValue();
        final String value = data.getInstance().getCurrentNode().textValue();
        if (!RhinoHelper.regMatch(regex, value))
            report.error(newMsg(data).msg(REGEX_NO_MATCH).put("regex", regex)
                .put("string", value));
    }

    @Override
    public String toString()
    {
        return keyword;
    }
}
