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
public final class FullValidationReport
    extends ValidationReport
{
    private final String prefix;
    private final List<String> messages = new LinkedList<String>();

    /**
     * Constructor of a validator which will prepend all messages added to it
     *
     * @param prefix the prefix which will appear before all messages
     */
    public FullValidationReport(final String prefix)
    {
        this.prefix = prefix;
    }

    @Override
    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public void message(final String message)
    {
        if (status != ValidationStatus.ERROR)
            messages.add(prefix + ": " + message);
    }

    @Override
    public void fail()
    {
        status = ValidationStatus.worstOf(status, ValidationStatus.FAILURE);
    }

    @Override
    public void fail(final String message)
    {
        fail();
        message(message);
    }

    @Override
    public void error(final String message)
    {
        status = ValidationStatus.ERROR;
        messages.clear();
        messages.add(prefix + ": FATAL: " + message);
    }

    @Override
    public void mergeWith(final ValidationReport other)
    {
        status = ValidationStatus.worstOf(status, other.status);

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
