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

package org.eel.kitchen.jsonschema.base;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.main.ValidationContext;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class MatchAllValidator
    implements Validator
{
    private final List<Validator> validators
        = new LinkedList<Validator>();

    public MatchAllValidator(final Collection<Validator> c)
    {
        validators.addAll(c);
    }

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();

        for (final Validator v: validators) {
            report.mergeWith(v.validate(context, instance));
            if (!report.isSuccess())
                break;
        }

        return report;
    }
}
