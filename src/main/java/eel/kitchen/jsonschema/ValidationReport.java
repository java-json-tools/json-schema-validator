/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema;

import eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import org.codehaus.jackson.JsonNode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static eel.kitchen.jsonschema.ValidationStatus.*;

/**
 * An object containing the validation status of an instance against a schema.
 * This is what is returned by {@link JsonValidator#validate(JsonNode)}.
 */
//TODO: separate failure message queue from error message queue
public final class ValidationReport
{
    private final String path;
    private final List<String> messages = new LinkedList<String>();
    private ValidationStatus status = SUCCESS;

    /**
     * Default constructor. In practice, it is never used,
     * except by {@link AlwaysTrueValidator}.
     */
    public ValidationReport()
    {
        path = "";
    }

    /**
     * Constructor of a validator which will prepend all messages added to it
     * (using #addMessage) with a path and a colon.
     *
     * @param path the path which will appear before all messages
     */
    public ValidationReport(final String path)
    {
        this.path = path;
    }

    /**
     * Tells whether the validation was successful. If it wasn't,
     * you can use #getMessages to obtain the list of validation failures.
     *
     * @return {@link ValidationStatus#SUCCESS} on success, or...
     */
    public boolean isSuccess()
    {
        return status == SUCCESS;
    }

    /**
     * Tells whether the validation led up to a fatal error.
     *
     * @return true if so
     */
    public boolean isError()
    {
        return status == ERROR;
    }

    /**
     * Returns the list of validation errors collected by this report.
     *
     * @return an unmodifiable list of messages
     */
    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    /**
     * Add one message to this report, effectively reporting a failure.
     *
     * @param message The message to add
     */
    public void addMessage(final String message)
    {
        if (status == ERROR)
            return;
        status = FAILURE;
        messages.add(path + ": " + message);
    }

    /**
     * Report a fatal error message. In this case, all previous messages are
     * cleared and the message remains the only one in the list. Sets the
     * status of this report to {@link ValidationStatus#ERROR}.
     *
     * @param message the error message
     */
    public void error(final String message)
    {
        status = ERROR;
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
    public void mergeWith(final ValidationReport other)
    {
        switch (other.status) {
            case SUCCESS:
                return;
            case ERROR:
                messages.clear();
                // Fall through
            case FAILURE:
                messages.addAll(other.messages);
                if (status != ERROR)
                    status = other.status;
        }
    }
}
