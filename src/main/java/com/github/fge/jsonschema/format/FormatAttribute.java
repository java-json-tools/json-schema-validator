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

package com.github.fge.jsonschema.format;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;

import java.util.EnumSet;

/**
 * Interface for a format attribute validator
 */
public interface FormatAttribute
{
    /**
     * Return the set of JSON Schema types this format attribute applies to
     *
     * <p>It is important that this method be implemented correctly. Remind
     * that validation for a given format attribute and an instance which type
     * is not supported always succeeds.</p>
     *
     * @return the set of supported types
     */
    EnumSet<NodeType> supportedTypes();

    /**
     * Validate the instance against this format attribute
     *
     * @param report the report to use
     * @param bundle the message bundle to use
     * @param data the validation data
     * @throws ProcessingException an exception occurs (normally, never for a
     * format attribute)
     */
    void validate(final ProcessingReport report, final MessageBundle bundle,
        final FullData data)
        throws ProcessingException;
}
