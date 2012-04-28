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

package org.eel.kitchen.jsonschema.format.specifiers;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.format.CSSColorParser;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ErrorLocatingParseRunner;
import org.parboiled.support.ParsingResult;

/**
 * Attempt at validating a CSS 2.1 color ({@code color} format specification in
 * the draft). It is far from being perfect... Ideally, it should use something
 * like jStyleParser.
 */
public final class CSSColorValidator
    extends FormatValidator
{
    private static final Rule rule
        = Parboiled.createParser(CSSColorParser.class).CSSColor();

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final ValidationReport report = context.createReport();

        final String value = instance.textValue();

        final ParsingResult<?> result
            = new ErrorLocatingParseRunner(rule).run(value);

        if (result.hasErrors())
            report.message("string is not a valid CSS 2.1 color");

        return report;
    }
}
