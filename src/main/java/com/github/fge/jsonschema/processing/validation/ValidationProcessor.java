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

package com.github.fge.jsonschema.processing.validation;

import com.github.fge.jsonschema.processing.build.FullValidationContext;
import com.github.fge.jsonschema.keyword.validators.KeywordValidator;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;

public final class ValidationProcessor
    implements Processor<ValidationData, ProcessingReport>
{
    private final Processor<ValidationData, FullValidationContext> processor;

    public ValidationProcessor(
        final Processor<ValidationData, FullValidationContext> processor)
    {
        this.processor = processor;
    }


    @Override
    public ProcessingReport process(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        final FullValidationContext context = processor.process(report, input);
        final ValidationData data = context.getValidationData();

        for (final KeywordValidator validator: context)
            validator.validate(this, report, data);

        return report;
    }
}
