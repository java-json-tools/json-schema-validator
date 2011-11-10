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
import org.eel.kitchen.jsonschema.context.ValidationContext;

/**
 * Keyword validator for the {@code maxLength} keyword (draft section 5.18)
 */
public final class MaxLengthKeywordValidator
    extends SimpleKeywordValidator
{
    /**
     * Value for {@code maxLength}
     */
    private final int maxLength;

    public MaxLengthKeywordValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);
        maxLength = schema.get("maxLength").getIntValue();
    }

    @Override
    public void validateInstance()
    {
        if (instance.getTextValue().length() > maxLength)
            report.addMessage("string is longer than maxLength");
    }
}
