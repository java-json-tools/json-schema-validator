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

package com.github.fge.jsonschema.keyword.validator;

import com.github.fge.jsonschema.exceptions.InvalidInstanceException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.msgsimple.bundle.MessageBundle;

/**
 * Interface for a keyword validator
 *
 * <p>Some keywords may have to ask the validation process to validate some
 * subschemas for them -- and in fact, some keywords, such as {@code allOf},
 * {@code not} or {@code extends}, for instance, do this exclusively.</p>
 *
 * <p>Therefore they are passed the main validator (as a {@link Processor} as
 * an argument. They take the responsibility of building the appropriate {@link
 * FullData} and calling the processor again.</p>
 */
public interface KeywordValidator
{
    /**
     * Validate the instance
     *
     * @param processor the main validation processor
     * @param report the report to use
     * @param bundle the message bundle to use
     * @param data the validation data
     * @throws InvalidInstanceException instance is invalid, and the report has
     * been configured to throw an exception instead of logging errors
     */
    void validate(final Processor<FullData, FullData> processor,
        final ProcessingReport report, final MessageBundle bundle,
        final FullData data)
        throws ProcessingException;
}
