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

package com.github.fge.jsonschema.processors.validation;

import com.github.fge.jsonschema.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.messages.SyntaxMessages;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ProcessorChain;
import com.github.fge.jsonschema.processors.build.ValidatorBuilder;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.jsonschema.processors.digest.SchemaDigester;
import com.github.fge.jsonschema.processors.format.FormatProcessor;
import com.github.fge.jsonschema.processors.ref.RefResolver;
import com.github.fge.jsonschema.processors.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;

public final class ValidationChain
    implements Processor<SchemaContext, ValidatorList>
{
    private final Processor<SchemaHolder, SchemaHolder> refSyntax;
    private final Processor<SchemaContext, ValidatorList> processor;

    public ValidationChain(final RefResolver refResolver,
        final Library library, final boolean useFormat)
    {
        final SyntaxProcessor syntaxProcessor
            = new SyntaxProcessor(library.getSyntaxCheckers());

        refSyntax = ProcessorChain.startWith(refResolver)
            .chainWith(syntaxProcessor).getProcessor();

        final SchemaDigester digester
            = new SchemaDigester(library.getDigesters());
        final ValidatorBuilder builder
            = new ValidatorBuilder(library.getValidators());

        ProcessorChain<SchemaContext, ValidatorList> chain
            = ProcessorChain.startWith(digester).chainWith(builder);

        if (useFormat) {
            final FormatProcessor formatProcessor
                = new FormatProcessor(library.getFormatAttributes());
            chain = chain.chainWith(formatProcessor);
        }

        processor = chain.getProcessor();
    }

    @Override
    public ValidatorList process(final ProcessingReport report,
        final SchemaContext input)
        throws ProcessingException
    {
        final SchemaHolder in = new SchemaHolder(input.getSchema());
        final SchemaHolder out = refSyntax.process(report, in);
        if (!report.isSuccess())
            throw new InvalidSchemaException(new ProcessingMessage()
                .message(SyntaxMessages.INVALID_SCHEMA));
        final SchemaContext output = new SchemaContext(out.getValue(),
            input.getInstanceType());
        return processor.process(report, output);
    }

    @Override
    public String toString()
    {
        return refSyntax + " -> " + processor;
    }
}
