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

/**
 * Simple enumeration to report a validation status.
 */
public enum ValidationStatus
{
    /**
     * The validation was successful
     */
    SUCCESS(0),
    /**
     * The validation failed
     */
    FAILURE(1),
    /**
     * A fatal error occurred
     */
    ERROR(2);

    private final int level;

    ValidationStatus(final int level)
    {
        this.level = level;
    }

    private static ValidationStatus fromLevel(final int level)
    {
        switch (level) {
            case 0:
                return SUCCESS;
            case 1:
                return FAILURE;
            default:
                return ERROR;
        }
    }

    public static ValidationStatus worstOf(final ValidationStatus first,
        final ValidationStatus second)
    {
        return fromLevel(Math.max(first.level, second.level));
    }
}
