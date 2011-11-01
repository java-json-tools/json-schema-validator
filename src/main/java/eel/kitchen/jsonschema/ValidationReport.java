/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ValidationReport
{
    private final List<String> messages
        = new LinkedList<String>();
    private ValidationStatus status = ValidationStatus.SUCCESS;

    public boolean isSuccess()
    {
        return status == ValidationStatus.SUCCESS;
    }

    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(final String message)
    {
        status = ValidationStatus.FAILURE;
        messages.add(message);
    }

    public void addMessages(final List<String> other)
    {
        status = ValidationStatus.FAILURE;
        messages.addAll(other);
    }

    public void mergeWith(final ValidationReport other)
    {
        if (other.status == ValidationStatus.SUCCESS)
            return;
        messages.addAll(other.messages);
        status = ValidationStatus.FAILURE;
    }
}
