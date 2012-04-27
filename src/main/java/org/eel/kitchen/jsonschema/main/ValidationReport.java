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
     */
    public abstract void fail();

    /**
     * Add a message and set the status to failure at the same time
     *
     * @param message the message to add
     */
    public abstract void fail(final String message);

    /**
     * Merge this report with another report
     *
     * @param other the absorbed report
     * @return true if validation should continue
     */
    public abstract boolean mergeWith(final ValidationReport other);

    /**
     * Is the validation a success?
     *
     * @return true if {@link #status} is {@link ValidationStatus#SUCCESS}
     */
    public final boolean isSuccess()
    {
        return status == ValidationStatus.SUCCESS;
    }
}
