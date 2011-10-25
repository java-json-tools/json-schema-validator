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

package eel.kitchen.jsonschema.v2.schema;

import java.util.LinkedHashSet;
import java.util.Set;

abstract class AbstractMultiSchema
    extends AbstractSchema
{
    protected final Set<Schema> schemas = new LinkedHashSet<Schema>();
    protected Schema winner = null;

    AbstractMultiSchema(final Set<Schema> schemas)
    {
        if (schemas.isEmpty())
            throw new IllegalArgumentException("schema set is empty");

        this.schemas.addAll(schemas);
    }

    @Override
    public final Schema getSchema(final String path)
    {
        if (winner == null)
            throw new IllegalArgumentException(".getSchema() called on a "
                + "multi-schema without a winner");
        return winner.getSchema(path);
    }
}
