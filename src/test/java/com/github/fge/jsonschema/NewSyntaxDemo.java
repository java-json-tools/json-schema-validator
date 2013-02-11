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

package com.github.fge.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.library.syntax.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.processing.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree2;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JsonLoader;

import java.io.IOException;

public final class NewSyntaxDemo
{
    private NewSyntaxDemo()
    {
    }

    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode schema = JsonLoader.fromResource("/draftv4/schema");
        final SchemaTree tree = new CanonicalSchemaTree2(schema);
        final ValidationData data = new ValidationData(tree);

        final ProcessingReport report = new ListProcessingReport();

        final Processor<ValidationData, ValidationData> processor
            = new SyntaxProcessor(DraftV4SyntaxCheckerDictionary.get());

        processor.process(report, data);
    }
}
