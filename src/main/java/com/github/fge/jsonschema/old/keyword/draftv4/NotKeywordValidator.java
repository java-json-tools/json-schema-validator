/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.old.keyword.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.old.keyword.KeywordValidator;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.validator.JsonValidator;
import com.github.fge.jsonschema.validator.ValidationContext;

/**
 * Validator for the {@code not} keyword
 *
 * <p>An instance is valid against that keyword if and only if it is not valid
 * against the schema defined by this keyword.</p>
 */
public final class NotKeywordValidator
    extends KeywordValidator
{
    private final JsonNode subSchema;

    public NotKeywordValidator(final JsonNode schema)
    {
        super("not", NodeType.values());
        subSchema = schema.get(keyword);
    }

    @Override
    protected void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final ValidationReport subReport = new ValidationReport();
        final JsonValidator validator = context.newValidator(subSchema);

        validator.validate(context, subReport, instance);

        if (subReport.isSuccess()) {
            report.addMessage(newMsg()
                .setMessage("instance validates against a forbidden schema")
                .build());
            return;
        }

        if (subReport.hasFatalError())
            report.mergeWith(subReport);
    }

    @Override
    public String toString()
    {
        return "not: must not match enclosed schema";
    }
}
