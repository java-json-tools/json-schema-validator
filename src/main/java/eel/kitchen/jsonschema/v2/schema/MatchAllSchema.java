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

import eel.kitchen.jsonschema.v2.instance.Instance;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

final class MatchAllSchema
    extends AbstractMultiSchema
{
    MatchAllSchema(final Set<Schema> schemas)
    {
        super(schemas);
    }

    @Override
    public void validate(final ValidationState state, final Instance instance)
    {
        final ValidationState current = new ValidationState(state);

        for (final Schema schema: schemas) {
            schema.validate(current, instance);
            if (current.isFailure()) {
                state.mergeWith(current);
                return;
            }
            winner = schema;
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder("AND(");

        final Queue<Schema> queue = new ArrayDeque<Schema>(schemas);

        buf.append(queue.remove());

        while (!queue.isEmpty())
            buf.append(", ").append(queue.remove());

        return buf.append(")").toString();
    }
}
