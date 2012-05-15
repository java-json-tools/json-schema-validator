/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ErrorLocatingParseRunner;
import org.parboiled.support.ParsingResult;

/**
 * Validator for the {@code color} format specifier
 *
 * <p>Note: this validator limits itself to "web" color specifications,
 * in the sense that arguments to, say, {@code rgb(x,y,
 * z)} are constrainted to be in the range 0 to 255,
 * for instance. Theoretically, the CSS specification allows for any integer
 * .</p>
 */
public final class CSSColorFormatSpecifier
    extends FormatSpecifier
{
    private static final FormatSpecifier instance
        = new CSSColorFormatSpecifier();

    private static final Rule rule
        = Parboiled.createParser(CSSColorParser.class).CSSColor();

    private CSSColorFormatSpecifier()
    {
        super(NodeType.STRING);
    }

    public static FormatSpecifier getInstance()
    {
        return instance;
    }

    @Override
    void checkValue(final ValidationReport report, final JsonNode value)
    {
        final ParsingResult<?> result
            = new ErrorLocatingParseRunner(rule).run(value.textValue());

        if (result.hasErrors())
            report.addMessage("string is not a valid CSS 2.1 color");
    }
}
