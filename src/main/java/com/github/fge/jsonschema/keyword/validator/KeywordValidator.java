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

package com.github.fge.jsonschema.keyword.validator;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.exceptions.InvalidInstanceException;
import com.github.fge.jsonschema.processors.data.FullData;
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
