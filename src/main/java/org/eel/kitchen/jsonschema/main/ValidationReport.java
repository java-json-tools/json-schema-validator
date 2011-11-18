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

import java.util.Collections;
import java.util.List;

public abstract class ValidationReport
{
    protected ValidationStatus status = ValidationStatus.SUCCESS;

    public static final ValidationReport TRUE = new ValidationReport()
    {
        @Override
        public List<String> getMessages()
        {
            return Collections.emptyList();
        }

        @Override
        public void addMessage(final String message)
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

    public abstract List<String> getMessages();

    public abstract void addMessage(final String message);

    public abstract void error(final String message);

    public abstract void mergeWith(final ValidationReport other);

    public final boolean isSuccess()
    {
        return status == ValidationStatus.SUCCESS;
    }

    public final boolean isError()
    {
        return status == ValidationStatus.ERROR;
    }
}
