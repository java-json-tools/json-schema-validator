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

package com.github.fge.jsonschema.processing.syntax;

import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.collect.Maps;

import java.util.Map;

public final class SyntaxProcessor
    implements Processor<ValidationData, ValidationData>
{
    private final Map<String, SyntaxChecker> checkers = Maps.newTreeMap();

    /**
     * Process the input
     *
     * @param report the report to use while processing
     * @param input the input for this processor
     * @return the output
     * @throws ProcessingException processing failed
     */
    @Override
    public ValidationData process(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        final ValidationData data
            = new ValidationData(input.getSchema().copy());
        validate(report, data);
        return input;
    }

    public void validate(final ProcessingReport report,
        final ValidationData data)
    {
        // TODO

    }


}
