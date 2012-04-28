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
import java.util.LinkedList;
import java.util.List;

/**
 * A validation report collecting all messages added to it
 */
//TODO: separate failure message queue from error message queue
public final class ValidationReport
{
    private final String prefix;
    private final List<String> messages = new LinkedList<String>();

    /**
     * Constructor of a validator which will prepend all messages added to it
     *
     * @param prefix the prefix which will appear before all messages
     */
    public ValidationReport(final String prefix)
    {
        this.prefix = prefix;
    }

    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    public void message(final String message)
    {
        messages.add(prefix + ": " + message);
    }

    public void fail()
    {
    }

    public void fail(final String message)
    {
        fail();
        message(message);
    }

    public boolean mergeWith(final ValidationReport other)
    {
        messages.addAll(other.getMessages());
        return false;
    }

    public boolean isSuccess() {
        return messages.isEmpty();
    }
}
