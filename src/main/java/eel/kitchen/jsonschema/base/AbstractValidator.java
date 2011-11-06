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

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * The base implementation from which all validators are derived
 */
public abstract class AbstractValidator
    implements Validator
{
    /**
     * The list of validators spawned by this validator, as a {@link Queue}
     */
    protected final Queue<Validator> queue = new ArrayDeque<Validator>();

    /**
     * Checks whether #queue has elements left
     *
     * @return true if there are elements left
     */
    @Override
    public boolean hasMoreElements()
    {
        return !queue.isEmpty();
    }

    /**
     * Returns the next validator from the queue
     *
     * @return a {@link Validator}
     * @throws NoSuchElementException if the queue is empty
     */
    @Override
    public Validator nextElement()
    {
        if (!hasMoreElements())
            throw new NoSuchElementException();

        return queue.remove();
    }
}
