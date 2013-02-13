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

package com.github.fge.jsonschema.validation;

import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.library.SchemaVersion;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.Thawed;
import com.google.common.collect.Maps;

import java.util.Map;

public final class ValidationConfigurationBuilder
    implements Thawed<ValidationConfiguration>
{
    final Map<JsonRef, Library> libraries;

    ValidationConfigurationBuilder()
    {
        libraries = Maps.newHashMap();
        for (final SchemaVersion version: SchemaVersion.values())
            libraries.put(version.getLocation(), version.getLibrary());
    }

    ValidationConfigurationBuilder(final ValidationConfiguration cfg)
    {
        libraries = Maps.newHashMap(cfg.libraries);
    }

    @Override
    public ValidationConfiguration freeze()
    {
        return new ValidationConfiguration(this);
    }
}
