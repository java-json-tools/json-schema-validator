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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import joptsimple.HelpFormatter;
import joptsimple.OptionDescriptor;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Main
{
    private static final int ALL_OK = 0;
    private static final int CMD_ERROR = 2;
    private static final int VALIDATION_FAILURE = 100;
    private static final int SCHEMA_SYNTAX_ERROR = 101;

    private static final String LINE_SEPARATOR
        = System.getProperty("line.separator", "\n");

    private static final Joiner JOINER = Joiner.on(LINE_SEPARATOR);

    private static final Joiner OPTIONS_JOINER = Joiner.on(", ");

    private static final String HELP_PREAMBLE
        = "Syntax: java -jar jsonschema.jar [options] file [file...]";

    private static final HelpFormatter HELP = new CustomHelpFormatter();

    private static final ObjectMapper MAPPER = JacksonUtils.newMapper();

    private final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    private final SyntaxValidator syntaxValidator
        = factory.getSyntaxValidator();

    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final OptionParser parser = new OptionParser();
        parser.accepts("syntax",
            "check the syntax of schema(s) given as argument(s)");
        parser.accepts("help", "show this help").forHelp();
        parser.formatHelpWith(HELP);

        final OptionSet optionSet;
        final boolean isSyntax;
        final int requiredArgs;

        try {
            optionSet = parser.parse(args);
            if (optionSet.has("help")) {
                parser.printHelpOn(System.out);
                System.exit(ALL_OK);
            }
        } catch (OptionException e) {
            System.err.println("unrecognized option(s): "
                + OPTIONS_JOINER.join(e.options()));
            parser.printHelpOn(System.err);
            System.exit(CMD_ERROR);
            throw new IllegalStateException("WTF??");
        }

        isSyntax = optionSet.has("syntax");
        requiredArgs = isSyntax ? 1 : 2;

        @SuppressWarnings("unchecked")
        final List<String> arguments
            = (List<String>) optionSet.nonOptionArguments();

        if (arguments.size() < requiredArgs) {
            System.err.println("missing arguments");
            parser.printHelpOn(System.err);
            System.exit(CMD_ERROR);
        }

        final List<File> files = Lists.newArrayList();
        for (final String target: arguments)
            files.add(new File(target).getCanonicalFile());

        new Main().proceed(isSyntax, files);
    }

    private void proceed(final boolean isSyntax, final List<File> files)
        throws IOException, ProcessingException
    {
        System.exit(isSyntax ? doSyntax(files): doValidation(files));
    }

    private int doSyntax(final List<File> files)
        throws IOException
    {
        final Reporter reporter = Reporters.DEFAULT;
        int retcode, ret = ALL_OK;
        String fileName;
        JsonNode node;

        for (final File file: files) {
            fileName = file.toString();
            node = MAPPER.readTree(file);
            retcode = reporter.validateSchema(syntaxValidator, fileName, node);
            if (retcode != ALL_OK)
                ret = retcode;
        }

        return ret;
    }

    private int doValidation(final List<File> files)
        throws IOException, ProcessingException
    {
        final Reporter reporter = Reporters.DEFAULT;
        final File schemaFile = files.remove(0);
        final JsonNode node = MAPPER.readTree(schemaFile);

        if (!syntaxValidator.schemaIsValid(node)) {
            System.err.println("Schema is invalid! Aborting...");
            return SCHEMA_SYNTAX_ERROR;
        }

        final JsonSchema schema = factory.getJsonSchema(node);

        int ret = ALL_OK, retcode;
        for (final File file: files) {
            retcode = reporter.validateInstance(schema, file);
            if (retcode != ALL_OK)
                ret = retcode;
        }

        return ret;
    }


    private static final class CustomHelpFormatter
        implements HelpFormatter
    {
        private final List<String> lines = Lists.newArrayList();

        @Override
        public String format(
            final Map<String, ? extends OptionDescriptor> options)
        {
            final Set<OptionDescriptor> opts
                = new LinkedHashSet<OptionDescriptor>(options.values());

            lines.add(HELP_PREAMBLE);
            lines.add("");
            lines.add("Options: ");

            StringBuilder sb;

            for (final OptionDescriptor descriptor: opts) {
                if (descriptor.representsNonOptions())
                    continue;
                sb = new StringBuilder().append('\t')
                    .append(optionsToString(descriptor.options()))
                    .append(": ").append(descriptor.description());
                lines.add(sb.toString());
            }

            lines.add("");
            lines.add("Exit codes:");
            lines.add("\t0: validation successful;");
            lines.add("\t1: exception occurred (appears on stderr)");
            lines.add("\t2: command line syntax error (missing argument, etc)");
            lines.add("\t100: one or more file(s) failed validation");

            return JOINER.join(lines) + LINE_SEPARATOR;
        }

        private String optionsToString(final Collection<String> names)
        {
            final List<String> list = Lists.newArrayList();
            for (final String name: names)
                list.add("--" + name);
            return OPTIONS_JOINER.join(list);
        }
    }

    private interface Reporter
    {
        int validateSchema(final SyntaxValidator validator,
            final String fileName, final JsonNode node)
            throws IOException;
        int validateInstance(final JsonSchema schema, final File file)
            throws IOException, ProcessingException;
    }

    private enum Reporters
        implements Reporter
    {
        DEFAULT
        {
            @Override
            public int validateSchema(final SyntaxValidator validator,
                final String fileName, final JsonNode node)
                throws IOException
            {
                final ListProcessingReport report
                    = (ListProcessingReport) validator.validateSchema(node);
                final boolean success = report.isSuccess();
                System.out.println("--- BEGIN " + fileName + "---");
                System.out.println("validation: " + (success ? "SUCCESS"
                    : "FAILURE"));
                if (!success)
                    System.out.println(JacksonUtils.prettyPrint(report
                        .asJson()));
                System.out.println("--- END " + fileName + "---");
                return success ? ALL_OK : SCHEMA_SYNTAX_ERROR;
            }

            @Override
            public int validateInstance(final JsonSchema schema,
                final File file)
                throws IOException, ProcessingException
            {
                final JsonNode node = MAPPER.readTree(file);
                final ListProcessingReport report
                    = (ListProcessingReport) schema.validate(node);
                final boolean success = report.isSuccess();
                System.out.println("--- BEGIN " + file + "---");
                System.out.println("validation: " + (success ? "SUCCESS"
                    : "FAILURE"));
                if (!success)
                    System.out.println(JacksonUtils.prettyPrint(report
                        .asJson()));
                System.out.println("--- END " + file + "---");
                return success ? ALL_OK : VALIDATION_FAILURE;
            }
        }
    }
}
