/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import joptsimple.HelpFormatter;
import joptsimple.OptionDescriptor;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Main
{
    private static final String LINE_SEPARATOR
        = System.getProperty("line.separator", "\n");
    private static final Joiner JOINER = Joiner.on(LINE_SEPARATOR);
    private static final Joiner OPTIONS_JOINER = Joiner.on(", ");
    private static final String HELP_PREAMBLE
        = "Syntax: java -jar json-schema-validator-full.jar [options] " +
        "file [file...]";

    private static final HelpFormatter HELP = new HelpFormatter()
    {
        private final List<String> lines = Lists.newArrayList();

        @Override
        public String format(
            final Map<String, ? extends OptionDescriptor> options)
        {
            final Set<OptionDescriptor> opts
                = new LinkedHashSet<OptionDescriptor>(options.values());

            lines.add(HELP_PREAMBLE);
            lines.add("Options: ");

            StringBuilder sb;

            for (final OptionDescriptor descriptor: opts) {
                if (descriptor.representsNonOptions())
                    continue;
                sb = new StringBuilder(optionsToString(descriptor.options()))
                    .append(": ").append(descriptor.description());
                lines.add(sb.toString());
            }
            return JOINER.join(lines) + LINE_SEPARATOR;
        }

        private String optionsToString(final Collection<String> names)
        {
            final List<String> list = Lists.newArrayList();
            for (final String name: names)
                list.add("--" + name);
            return OPTIONS_JOINER.join(list);
        }
    };

    public static void main(final String... args)
        throws IOException
    {
        final OptionParser parser = new OptionParser();
        parser.accepts("syntax",
            "check the syntax of schema(s) given as argument(s)");
        parser.formatHelpWith(HELP);
        try {
            final OptionSet optionSet = parser.parse(args);
            if (optionSet.nonOptionArguments().isEmpty()) {
                System.err.println("missing arguments");
                parser.printHelpOn(System.err);
                System.exit(2);
            }
        } catch (OptionException e) {
            System.err.println(
                "unrecognized option: " + e.options().iterator().next());
            parser.printHelpOn(System.err);
            System.exit(2);
        }
        final String input = "{" +
            "\"$schema\": \"http://json-schema.org/draft-04/hyper-schema#\"," +
            "\"links\":null" +
            "}";
        final JsonNode node = JsonLoader.fromString(input);
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final SyntaxValidator validator = factory.getSyntaxValidator();
        final ProcessingReport report = validator.validateSchema(node);
        System.out.println(report);
    }
}
