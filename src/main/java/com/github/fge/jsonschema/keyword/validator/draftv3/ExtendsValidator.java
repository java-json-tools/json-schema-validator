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

package com.github.fge.jsonschema.keyword.validator.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.JsonSchemaTree;

public final class ExtendsValidator
    extends AbstractKeywordValidator
{
    private static final JsonPointer BASE_PTR
        = JsonPointer.empty().append("extends");

    public ExtendsValidator(final JsonNode digest)
    {
        super("extends");
    }

    @Override
    public void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        final JsonSchemaTree schemaTree = data.getSchema();
        final JsonNode node = schemaTree.getCurrentNode().get(keyword);

        if (node.isObject()) {
            schemaTree.append(BASE_PTR);
            processor.process(report, data);
            schemaTree.pop();
            return;
        }

        /*
         * Not an object? An array
         */
        final int size = node.size();

        for (int index = 0; index < size; index++) {
            schemaTree.append(BASE_PTR.append(index));
            processor.process(report, data);
            schemaTree.pop();
        }
    }

    @Override
    public String toString()
    {
        return keyword;
    }

}
