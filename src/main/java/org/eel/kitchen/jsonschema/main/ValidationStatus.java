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

/**
 * Simple enumeration to report a validation status.
 */
public enum ValidationStatus
{
    /**
     * The validation was successful
     */
    SUCCESS,
    /**
     * The validation failed
     */
    FAILURE,
    /**
     * A fatal error occurred
     */
    ERROR;

    /**
     * Given two validation statuses, return the worst one of them both
     *
     * @param first the first
     * @param second the second
     * @return the worst
     */
    public static ValidationStatus worstOf(final ValidationStatus first,
        final ValidationStatus second)
    {
        /*
         * Order is crucial here!
         */
        return values()[Math.max(first.ordinal(), second.ordinal())];
    }
}
