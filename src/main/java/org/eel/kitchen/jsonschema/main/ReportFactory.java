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
 * A report generator
 *
 * <p>Will soon be given the boot!</p>
 */
public final class ReportFactory
{
    /**
     * Will be removed soon
     */
    private final boolean failFast;

    /**
     * Constructor
     *
     * @param failFast value of {@link #failFast}
     */
    public ReportFactory(final boolean failFast)
    {
        this.failFast = failFast;
    }

    /**
     * Create a report with a prefix prepended to all messages
     *
     * @param prefix the prefix
     * @return the report
     */
    public ValidationReport create(final String prefix)
    {
        return  new FullValidationReport(prefix);

    }
}
