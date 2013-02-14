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
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.digest.DraftV4DigesterDictionary;
import com.github.fge.jsonschema.library.syntax.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.library.validator.DraftV4ValidatorDictionary;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.load.LoadingConfiguration;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ProcessorChain;
import com.github.fge.jsonschema.processors.build.ValidatorBuilder;
import com.github.fge.jsonschema.processors.data.FullValidationContext;
import com.github.fge.jsonschema.processors.data.ValidationData;
import com.github.fge.jsonschema.processors.digest.SchemaDigester;
import com.github.fge.jsonschema.processors.ref.RefResolverProcessor;
import com.github.fge.jsonschema.processors.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;

import java.io.IOException;
import java.util.Map;

public final class NewAPIPerfTest
{
    private NewAPIPerfTest()
    {
    }

    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode googleAPI
            = JsonLoader.fromResource("/other/google-json-api.json");
        final Map<String, JsonNode> googleSchemas
            = JacksonUtils.asMap(googleAPI.get("schemas"));

        final LoadingConfiguration cfg = LoadingConfiguration.newConfiguration()
            .dereferencing(Dereferencing.INLINE).freeze();
        final SchemaLoader loader = new SchemaLoader(cfg);
        final RefResolverProcessor p1 = new RefResolverProcessor(loader);
        final SyntaxProcessor p2
            = new SyntaxProcessor(DraftV4SyntaxCheckerDictionary.get());
        final SchemaDigester p3
            = new SchemaDigester(DraftV4DigesterDictionary.get());
        final ValidatorBuilder p4
            = new ValidatorBuilder(DraftV4ValidatorDictionary.get());

        final Processor<ValidationData, FullValidationContext> chain
            = ProcessorChain.startWith(p1).chainWith(p2).chainWith(p3)
                .chainWith(p4).getProcessor();

        final ValidationProcessor processor = new ValidationProcessor(chain);

        final JsonNode draftV4CoreSchema
            = JsonLoader.fromResource("/draftv4/schema");
        final SchemaTree schemaTree = loader.load(draftV4CoreSchema);

        long begin, current;
        begin = System.currentTimeMillis();
        doValidate(googleSchemas, processor, schemaTree, -1);
        current = System.currentTimeMillis();

        System.out.println("Initial validation :" + (current - begin) + " ms");

        begin = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            doValidate(googleSchemas, processor, schemaTree, i);
            if (i % 20 == 0) {
                current = System.currentTimeMillis();
                System.out.println(String.format("Iteration %d (in %d ms)", i,
                    current - begin));
            }
        }

        final long end = System.currentTimeMillis();
        System.out.println("END -- time in ms: " + (end - begin));
        System.out.println("ref: " + p1);
        System.out.println("digest: " + p3);
        System.out.println("build: " + p4);
        System.out.println("validation: " + processor);
        System.exit(0);
    }

    private static void doValidate(final Map<String, JsonNode> schemas,
        final ValidationProcessor processor, final SchemaTree tree,
        final int i)
        throws ProcessingException
    {
        String name;
        JsonNode value;
        ValidationData data;
        ProcessingReport report;

        for (final Map.Entry<String, JsonNode> entry: schemas.entrySet()) {
            name = entry.getKey();
            value = entry.getValue();
            data = new ValidationData(tree, new SimpleJsonTree(value));
            report = new ListProcessingReport();
            processor.process(report, data);
            if (!report.isSuccess()) {
                System.err.println("ERROR: schema " + name + " did not "
                    + "validate (iteration " + i + ')');
                System.exit(1);
            }
        }
    }
}
