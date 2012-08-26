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

package org.eel.kitchen.jsonschema.main;

import org.eel.kitchen.jsonschema.validator.JsonValidatorCache;

/**
 * A validation context
 *
 * <p>This object is passed along the validation process. At any point in
 * the validation process, it contains the current context and the
 * underlying factory.</p>
 *
 * <p>The latter is necessary since four keywords may have to spawn other
 * validators: {@code type}, {@code disallow}, {@code dependencies} and
 * {@code extends}.</p>
 */
public final class ValidationContext
{
    private SchemaContainer container;
    private final JsonValidatorCache cache;

    public ValidationContext(final JsonValidatorCache cache)
    {
        this.cache = cache;
    }

    public JsonValidatorCache getValidatorCache()
    {
        return cache;
    }

    public SchemaContainer getContainer()
    {
        return container;
    }

    public void setContainer(final SchemaContainer container)
    {
        this.container = container;
    }
}
