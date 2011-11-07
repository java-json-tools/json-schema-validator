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

import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.codehaus.jackson.JsonNode;

/**
 * A {@link Validator} implementation which spawns other validators. It is
 * used by {@link KeywordValidator} implementations which cannot operate in
 * one pass, but need to build further validators.
 */
public abstract class CombinedValidator
    extends KeywordValidator
{
    protected CombinedValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);
    }
}
