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

package eel.kitchen.jsonschema.base;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.context.ValidationContext;
import org.codehaus.jackson.JsonNode;

public final class AlwaysTrueValidator
    extends AbstractValidator
{
    private final ValidationReport report;

    // syntax
    public AlwaysTrueValidator(final ValidationContext context)
    {
        report = context.createReport();
    }

    // keyword
    public AlwaysTrueValidator(final ValidationContext context,
        final JsonNode instance)
    {
        report = context.createReport();
    }

    public AlwaysTrueValidator(final ValidationReport report,
        final JsonNode instance)
    {
        this.report = report;
    }

    @Override
    public ValidationReport validate()
    {
        return report;
    }
}
