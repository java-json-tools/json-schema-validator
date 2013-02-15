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
import com.github.fge.jsonschema.processing.ProcessorSelector;
import com.github.fge.jsonschema.processors.build.ValidatorBuilder;
import com.github.fge.jsonschema.processors.data.FullValidationContext;
import com.github.fge.jsonschema.processors.data.ValidationContext;
import com.github.fge.jsonschema.processors.digest.SchemaDigester;
import com.github.fge.jsonschema.processors.format.FormatProcessor;
import com.github.fge.jsonschema.processors.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.base.Predicate;

public final class ValidationChain
    implements Processor<ValidationContext, FullValidationContext>
{
    private final Processor<ValidationContext, FullValidationContext> processor;

    public ValidationChain(final Library library, final boolean useFormat)
    {
        final SyntaxProcessor syntaxProcessor
            = new SyntaxProcessor(library.getSyntaxCheckers());

        final Processor<ValidationContext, FullValidationContext> onSuccess
            = mainProcessor(library, useFormat);

        final ProcessorSelector<ValidationContext, FullValidationContext> selector
            = new ProcessorSelector<ValidationContext, FullValidationContext>()
            .when(schemaIsValid()).then(onSuccess)
            .otherwise(onFailure());

        processor = ProcessorChain.startWith(syntaxProcessor)
            .chainWith(selector.getProcessor()).getProcessor();
    }

    public ValidationChain(final Library library)
    {
        this(library, true);
    }

    @Override
    public FullValidationContext process(final ProcessingReport report,
        final ValidationContext input)
        throws ProcessingException
    {
        return processor.process(report, input);
    }

    private static Processor<ValidationContext, FullValidationContext> onFailure()
    {
        return new Processor<ValidationContext, FullValidationContext>()
        {
            @Override
            public FullValidationContext process(final ProcessingReport report,
                final ValidationContext input)
                throws ProcessingException
            {
                final ProcessingMessage message = input.newMessage()
                    .message(SyntaxMessages.INVALID_SCHEMA);
                throw new InvalidSchemaException(message);
            }
        };
    }

    private static Predicate<ValidationContext> schemaIsValid()
    {
        return new Predicate<ValidationContext>()
        {
            @Override
            public boolean apply(final ValidationContext input)
            {
                return input.getSchema().isValid();
            }
        };
    }

    private static Processor<ValidationContext, FullValidationContext>
        mainProcessor(final Library library, final boolean useFormat)
    {
        final SchemaDigester digester
            = new SchemaDigester(library.getDigesters());
        final ValidatorBuilder builder
            = new ValidatorBuilder(library.getValidators());
        ProcessorChain<ValidationContext, FullValidationContext> chain
            = ProcessorChain.startWith(digester).chainWith(builder);

        if (useFormat) {
            final FormatProcessor formatProcessor
                = new FormatProcessor(library.getFormatAttributes());
            chain = chain.chainWith(formatProcessor);
        }

        return chain.getProcessor();
    }

    @Override
    public String toString()
    {
        return processor.toString();
    }
}
