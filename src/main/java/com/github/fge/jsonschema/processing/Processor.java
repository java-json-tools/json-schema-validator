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

package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingReport;

/**
 * Interface for a processor in JSON Schema processing
 *
 * <p>Note that it is required that both inputs and outputs implement {@link
 * MessageProvider}: this allows the processor to grab a context-dependent
 * message to include into the report should the need arise. A {@link
 * ProcessingReport} is passed as an argument so that the processor can add
 * debug/info/warning/error messages.</p>
 *
 * @param <IN> input type for that processor
 * @param <OUT> output type for that processor
 */
public interface Processor<IN extends MessageProvider, OUT extends MessageProvider>
{
    /**
     * Process the input
     *
     * @param report the report to use while processing
     * @param input the input for this processor
     * @return the output
     * @throws ProcessingException processing failed
     */
    OUT process(final ProcessingReport report, final IN input)
        throws ProcessingException;
}
