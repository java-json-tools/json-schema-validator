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
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;

public final class ExtendsValidator
    extends AbstractKeywordValidator
{
    public ExtendsValidator(final JsonNode digest)
    {
        super("extends");
    }

    @Override
    public void validate(final Processor<FullData, ProcessingReport> processor,
        final ProcessingReport report, final FullData data)
        throws ProcessingException
    {
        final SchemaTree tree = data.getSchema();
        final JsonNode node = tree.getNode().get(keyword);

        FullData newData;

        if (node.isObject()) {
            newData = data.withSchema(tree.append(JsonPointer.of(keyword)));
            processor.process(report, newData);
            return;
        }

        /*
         * Not an object? An array
         */
        final int size = node.size();
        JsonPointer pointer;

        for (int index = 0; index < size; index++) {
            pointer = JsonPointer.of(keyword, index);
            newData = data.withSchema(tree.append(pointer));
            processor.process(report, newData);
        }
    }

    @Override
    public String toString()
    {
        return keyword;
    }

}
