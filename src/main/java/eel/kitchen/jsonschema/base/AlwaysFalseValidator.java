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
import eel.kitchen.jsonschema.keyword.KeywordValidator;
import eel.kitchen.jsonschema.syntax.SyntaxValidator;

/**
 * A {@link Validator} which always reports a failure.
 */
public final class AlwaysFalseValidator
    extends AbstractValidator
    implements SyntaxValidator, KeywordValidator
{
    /**
     * The report
     */
    private final ValidationReport report;

    /**
     * Constructor, which only takes a {@link ValidationReport} as an
     * argument. It is up to the caller to ensure that this report actually
     * reports a failure!
     *
     * @param report the report to use
     */
    public AlwaysFalseValidator(final ValidationReport report)
    {
        this.report = report;
    }

    @Override
    public ValidationReport validate()
    {
        return report;
    }
}
