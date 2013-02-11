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

package com.github.fge.jsonschema.format;

import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processors.data.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;

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
     * @param data the validation data
     * @throws ProcessingException an exception occurs (normally, never for a
     * format attribute)
     */
    void validate(final ProcessingReport report, ValidationData data)
        throws ProcessingException;
}
