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
import org.codehaus.jackson.JsonNode;

/**
 * A {@link Validator} which is always true. As it is potentially registered
 * as both a {@link KeywordValidator}, a {@link SyntaxValidator} or a format
 * validator, it has constructors matching all three.
 */
public final class AlwaysTrueValidator
    extends AbstractValidator
    implements KeywordValidator
{
    /**
     * The report
     */
    private final ValidationReport report;

    /**
     * The constructor matching a format validator
     *
     * @param report the report to use
     * @param instance the instance (ignored)
     */
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
