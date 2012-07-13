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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.util.NodeType;

/**
 * Base class for all keywords accepting a positive integer as an argument
 */
public abstract class PositiveIntegerKeywordValidator
    extends KeywordValidator
{
    /**
     * Value for this keyword
     */
    protected final int intValue;

    /**
     * Protected constructor
     *
     * @param keyword the schema keyword
     * @param schema the schema
     * @param types instance types validated by this keyword
     */
    protected PositiveIntegerKeywordValidator(final String keyword,
        final JsonNode schema, final NodeType... types)
    {
        super(types);
        intValue = schema.get(keyword).intValue();
    }
}
