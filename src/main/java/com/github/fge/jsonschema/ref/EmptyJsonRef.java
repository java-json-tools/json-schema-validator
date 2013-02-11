/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.ref;

/**
 * A completely empty JSON Reference (ie, {@code #})
 *
 * <p>This happens in a lot of situations, it is therefore beneficial to have
 * a dedicated class for it. For instance, resolving any other reference against
 * this one always returns the other reference, and it is never absolute.</p>
 */
final class EmptyJsonRef
    extends JsonRef
{
    private static final JsonRef INSTANCE = new EmptyJsonRef();

    private EmptyJsonRef()
    {
        super(HASHONLY_URI);
    }

    static JsonRef getInstance()
    {
        return INSTANCE;
    }

    @Override
    public boolean isAbsolute()
    {
        return false;
    }

    @Override
    public JsonRef resolve(final JsonRef other)
    {
        return other;
    }
}
