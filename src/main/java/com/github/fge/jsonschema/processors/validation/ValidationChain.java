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

import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.messages.SyntaxMessages;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ProcessorChain;
import com.github.fge.jsonschema.processing.ProcessorSelector;
import com.github.fge.jsonschema.processors.build.ValidatorBuilder;
import com.github.fge.jsonschema.processors.data.FullValidationContext;
import com.github.fge.jsonschema.processors.data.ValidationData;
import com.github.fge.jsonschema.processors.digest.SchemaDigester;
import com.github.fge.jsonschema.processors.format.FormatProcessor;
import com.github.fge.jsonschema.processors.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.base.Predicate;

public final class ValidationChain
    implements Processor<ValidationData, FullValidationContext>
{
    private final Processor<ValidationData, FullValidationContext> processor;

    public ValidationChain(final Library library)
    {
        final SyntaxProcessor syntaxProcessor
            = new SyntaxProcessor(library.getSyntaxCheckers());
        final SchemaDigester digester
            = new SchemaDigester(library.getDigesters());
        final ValidatorBuilder builder
            = new ValidatorBuilder(library.getValidators());
        final FormatProcessor formatProcessor
            = new FormatProcessor(library.getFormatAttributes());

        final Processor<ValidationData, FullValidationContext> onSuccess
            = ProcessorChain.startWith(digester).chainWith(builder)
                .chainWith(formatProcessor).end();

        final ProcessorSelector<ValidationData, FullValidationContext> selector
            = new ProcessorSelector<ValidationData, FullValidationContext>()
                .when(schemaIsValid()).then(onSuccess)
                .otherwise(onFailure());

        processor = ProcessorChain.startWith(syntaxProcessor)
            .chainWith(selector.getProcessor()).end();
    }

    @Override
    public FullValidationContext process(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        return processor.process(report, input);
    }

    private static Processor<ValidationData, FullValidationContext> onFailure()
    {
        return new Processor<ValidationData, FullValidationContext>()
        {
            @Override
            public FullValidationContext process(final ProcessingReport report,
                final ValidationData input)
                throws ProcessingException
            {
                final ProcessingMessage message = input.newMessage()
                    .message(SyntaxMessages.INVALID_SCHEMA);
                throw new ProcessingException(message);
            }
        };
    }

    private static Predicate<ValidationData> schemaIsValid()
    {
        return new Predicate<ValidationData>()
        {
            @Override
            public boolean apply(final ValidationData input)
            {
                return input.getSchema().isValid();
            }
        };
    }
}
