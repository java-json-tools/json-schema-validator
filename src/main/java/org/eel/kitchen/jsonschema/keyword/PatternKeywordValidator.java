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

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.util.RhinoHelper;

/**
 * <p>Keyword validator for the {@code pattern} keyword (draft version 5.16).
 * </p>
 *
 * <p>Note that the draft explicitly says that the regex should obey ECMA
 * 262, which means {@link java.util.regex} is unusable. We therefore use
 * rhino, which does have an ECMA 262 regex engine.</p>
 *
 * <p>And also note that "matching" is meant in the <b>real</b> sense of the
 * term. Don't be fooled by Java's {@link String#matches(String)}!
 * </p>
 *
 * @see RhinoHelper
 */
public final class PatternKeywordValidator
    extends KeywordValidator
{
    public PatternKeywordValidator()
    {
        super("pattern");
    }

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();
        final String regex = context.getSchemaNode().get(keyword)
            .getTextValue();

        if (!RhinoHelper.regMatch(regex, instance.getTextValue()))
            report.addMessage("string does not match specified regex");

        return report;
    }
}
