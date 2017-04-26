/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.main.JsonValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.collect.Lists;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collections;
import java.util.List;

/**
 * Processor for validating one schema/instance pair
 *
 * <p>One such processor is created for each schema/instance validation.</p>
 *
 * <p>Internally, all validation operations provided by the API (whether that
 * be a {@link JsonSchema}, via {@link JsonValidator} or using {@link
 * ValidationProcessor} directly) will eventually instantiate one of these. More
 * precisely, this is instantiated by {@link
 * ValidationProcessor#process(ProcessingReport, FullData)}.</p>
 *
 */
@NotThreadSafe
@ParametersAreNonnullByDefault
final class InstanceValidator
    implements Processor<FullData, FullData>
{
    private final MessageBundle syntaxMessages;
    private final MessageBundle validationMessages;
    private final Processor<SchemaContext, ValidatorList> keywordBuilder;

    private final ValidationStack stack;

    /**
     * Constructor -- do not use directly!
     *
     * @param syntaxMessages the syntax message bundle
     * @param validationMessages the validation message bundle
     * @param keywordBuilder the keyword builder
     */
    InstanceValidator(final MessageBundle syntaxMessages,
        final MessageBundle validationMessages,
        final Processor<SchemaContext, ValidatorList> keywordBuilder)
    {
        this.syntaxMessages = syntaxMessages;
        this.validationMessages = validationMessages;
        this.keywordBuilder = keywordBuilder;

        final String errmsg
            = validationMessages.getMessage("err.common.validationLoop");
        stack = new ValidationStack(errmsg);
    }

    @Override
    public FullData process(final ProcessingReport report,
        final FullData input)
        throws ProcessingException
    {
        /*
         * We don't want the same validation context to appear twice, see above
         */
        stack.push(input);


        /*
         * Build a validation context, attach a report to it
         */
        final SchemaContext context = new SchemaContext(input);

        /*
         * Get the full context from the cache. Inject the messages into the
         * main report.
         */
        final ValidatorList fullContext = keywordBuilder.process(report,
            context);

        if (fullContext == null) {
            final ProcessingMessage message = collectSyntaxErrors(report);
            throw new InvalidSchemaException(message);
        }

        /*
         * Get the calculated context. Build the data.
         */
        final SchemaContext newContext = fullContext.getContext();
        final FullData data = new FullData(newContext.getSchema(),
            input.getInstance(), input.isDeepCheck());

        /*
         * Validate against all keywords.
         */
        for (final KeywordValidator validator: fullContext)
            validator.validate(this, report, validationMessages, data);

        /*
         * At that point, if the report is a failure, we quit: there is no
         * reason to go any further. Unless the user has asked to continue even
         * in this case.
         */
        if (!(report.isSuccess() || data.isDeepCheck())) {
            stack.pop();
            return input;
        }

        /*
         * Now check whether this is a container node with a size greater than
         * 0. If not, no need to go see the children.
         */
        final JsonNode node = data.getInstance().getNode();

        if (node.isContainerNode()) {
            if (node.isArray())
                processArray(report, data);
            else
                processObject(report, data);
        }

        stack.pop();
        return input;
    }

    @Override
    public String toString()
    {
        return "instance validator";
    }

    private void processArray(final ProcessingReport report,
        final FullData input)
        throws ProcessingException
    {
        final SchemaTree tree = input.getSchema();
        final JsonTree instance = input.getInstance();

        final JsonNode schema = tree.getNode();
        final JsonNode node = instance.getNode();

        final JsonNode digest = ArraySchemaDigester.getInstance()
            .digest(schema);
        final ArraySchemaSelector selector = new ArraySchemaSelector(digest);

        final int size = node.size();

        FullData data;
        JsonTree newInstance;

        for (int index = 0; index < size; index++) {
            newInstance = instance.append(JsonPointer.of(index));
            data = input.withInstance(newInstance);
            for (final JsonPointer ptr: selector.selectSchemas(index)) {
                data = data.withSchema(tree.append(ptr));
                process(report, data);
            }
        }
    }

    private void processObject(final ProcessingReport report,
        final FullData input)
        throws ProcessingException
    {
        final SchemaTree tree = input.getSchema();
        final JsonTree instance = input.getInstance();

        final JsonNode schema = tree.getNode();
        final JsonNode node = instance.getNode();

        final JsonNode digest = ObjectSchemaDigester.getInstance()
            .digest(schema);
        final ObjectSchemaSelector selector = new ObjectSchemaSelector(digest);

        final List<String> fields = Lists.newArrayList(node.fieldNames());
        Collections.sort(fields);

        FullData data;
        JsonTree newInstance;

        for (final String field: fields) {
            newInstance = instance.append(JsonPointer.of(field));
            data = input.withInstance(newInstance);
            for (final JsonPointer ptr: selector.selectSchemas(field)) {
                data = data.withSchema(tree.append(ptr));
                process(report, data);
            }
        }
    }

    private ProcessingMessage collectSyntaxErrors(final ProcessingReport report)
    {
        /*
         * OK, that's for issue #99 but that's ugly nevertheless.
         *
         * We want syntax error messages to appear in the exception text.
         */
        final String msg = syntaxMessages.getMessage("core.invalidSchema");
        final ArrayNode arrayNode = JacksonUtils.nodeFactory().arrayNode();
        JsonNode node;
        for (final ProcessingMessage message: report) {
            node = message.asJson();
            if ("syntax".equals(node.path("domain").asText()))
                arrayNode.add(node);
        }
        final StringBuilder sb = new StringBuilder(msg);
        sb.append("\nSyntax errors:\n");
        sb.append(JacksonUtils.prettyPrint(arrayNode));
        return new ProcessingMessage().setMessage(sb.toString());
    }
}
