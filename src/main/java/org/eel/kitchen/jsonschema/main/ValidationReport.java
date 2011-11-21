/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.main;

import java.util.Collections;
import java.util.List;

/**
 * A validation report
 *
 * <p>Depending on the implementation, this will collect messages or
 * immediately return a {@link JsonValidationFailureException} if an error
 * is encountered.</p>
 */
public abstract class ValidationReport
{
    /**
     * The status of this report
     */
    protected ValidationStatus status = ValidationStatus.SUCCESS;

    /**
     * A validation report which is always true
     */
    public static final ValidationReport TRUE = new ValidationReport()
    {
        @Override
        public List<String> getMessages()
        {
            return Collections.emptyList();
        }

        @Override
        public void message(final String message)
        {
        }

        @Override
        public void fail()
        {
        }

        @Override
        public void fail(final String message)
        {
        }

        @Override
        public void error(final String message)
        {
        }

        @Override
        public void mergeWith(final ValidationReport other)
        {
        }
    };

    /**
     * Get the messages collected by this report
     *
     * @return the messages, in the order in which they were submitted
     */
    public abstract List<String> getMessages();

    /**
     * Add a message to this validator's message list
     *
     * @param message the message to add
     */
    public abstract void message(final String message);

    /**
     * Set the status of this validator to {@link ValidationStatus#FAILURE}
     *
     * @throws JsonValidationFailureException for {@link
     * FailFastValidationReport} instances
     */
    public abstract void fail()
        throws JsonValidationFailureException;

    /**
     * Add a message and set the status to failure at the same time
     *
     * @param message the message to add
     * @throws JsonValidationFailureException for {@link
     * FailFastValidationReport} instances
     */
    public abstract void fail(final String message)
        throws JsonValidationFailureException;

    /**
     * Add a message and set the status to {@link ValidationStatus#ERROR}
     *
     * <p>The difference with a failure is that reporting an error flushes
     * all messages already collected.</p>
     *
     * @param message the message to add
     * @throws JsonValidationFailureException for {@link
     * FailFastValidationReport} instances
     */
    public abstract void error(final String message)
        throws JsonValidationFailureException;

    /**
     * Merge this report with another report
     *
     * @param other the absorbed report
     */
    public abstract void mergeWith(final ValidationReport other);

    /**
     * Is the validation a success?
     *
     * @return true if {@link #status} is {@link ValidationStatus#SUCCESS}
     */
    public final boolean isSuccess()
    {
        return status == ValidationStatus.SUCCESS;
    }

    /**
     * Did a fatal error occur during validation?
     *
     * @return true if {@link #status} is {@link ValidationStatus#ERROR}
     */
    public final boolean isError()
    {
        return status == ValidationStatus.ERROR;
    }
}
