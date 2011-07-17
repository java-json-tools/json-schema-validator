/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.validators;

import org.codehaus.jackson.JsonNode;

/**
 * Empty schema provider. Spawned by every validator which is not an array or
 * object validator.
 */
public final class EmptySchemaProvider
    implements SchemaProvider
{
    /**
     * For this particular provider, this method should never be called.
     *
     * @param path The subpath
     * @return nothing...
     * @throws RuntimeException
     */
    @Override
    public JsonNode getSchemaForPath(final String path)
    {
        throw new RuntimeException("I should never be called!!");
    }
}
