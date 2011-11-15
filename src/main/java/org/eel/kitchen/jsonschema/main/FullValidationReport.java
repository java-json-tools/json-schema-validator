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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.main;

import org.codehaus.jackson.JsonNode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An object containing the validation status of an instance against a schema.
 * This is what is returned by {@link JsonValidator#validate(JsonNode)}.
 */
//TODO: separate failure message queue from error message queue
public final class FullValidationReport
    implements ValidationReport
{
    private final String path;
    private final List<String> messages = new LinkedList<String>();
    private ValidationStatus status = ValidationStatus.SUCCESS;

    /**
     * Default constructor. Only sets {@link #path} to an empty string.
     */
    public FullValidationReport()
    {
        path = "";
    }

    /**
     * Constructor of a validator which will prepend all messages added to it
     * (using #addMessage) with a path and a colon.
     *
     * @param path the path which will appear before all messages
     */
    public FullValidationReport(final String path)
    {
        this.path = path;
    }

    @Override
    public ValidationStatus getStatus()
    {
        return status;
    }

    /**
     * Tells whether the validation was successful. If it wasn't,
     * you can use #getMessages to obtain the list of validation failures.
     *
     * @return {@link ValidationStatus#SUCCESS} on success, or...
     */
    @Override
    public boolean isSuccess()
    {
        return status == ValidationStatus.SUCCESS;
    }

    /**
     * Tells whether the validation led up to a fatal error.
     *
     * @return true if so
     */
    @Override
    public boolean isError()
    {
        return status == ValidationStatus.ERROR;
    }

    /**
     * Returns the list of validation errors collected by this report.
     *
     * @return an unmodifiable list of messages
     */
    @Override
    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    /**
     * Add one message to this report, effectively reporting a failure.
     *
     * @param message The message to add
     */
    @Override
    public void addMessage(final String message)
    {
        if (status == ValidationStatus.ERROR)
            return;
        status = ValidationStatus.FAILURE;
        messages.add(path + ": " + message);
    }

    /**
     * Report a fatal error message. In this case, all previous messages are
     * cleared and the message remains the only one in the list. Sets the
     * status of this report to {@link ValidationStatus#ERROR}.
     *
     * @param message the error message
     */
    @Override
    public void error(final String message)
    {
        status = ValidationStatus.ERROR;
        messages.clear();
        messages.add(path + ": FATAL: " + message);
    }

    /**
     * Merge the current report with another report. In effect,
     * it adds all messages of the other report to the list of the current
     * report, and sets this report's {@link #status} to the other report's
     * status.
     *
     * @param other The other report
     */
    @Override
    public void mergeWith(final ValidationReport other)
    {
        status = ValidationStatus.worstOf(status, other.getStatus());

        switch (status) {
            case SUCCESS:
                return;
            case ERROR:
                messages.clear();
                // Fall through
            case FAILURE:
                messages.addAll(other.getMessages());
        }
    }
}
