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

import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.library.SchemaVersion;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.RefSanityChecks;
import com.github.fge.jsonschema.util.Thawed;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.github.fge.jsonschema.messages.ValidationConfigurationMessages.*;

public final class ValidationConfigurationBuilder
    implements Thawed<ValidationConfiguration>
{
    final Map<JsonRef, Library> libraries;
    Library defaultLibrary = SchemaVersion.DRAFTV4.getLibrary();

    ValidationConfigurationBuilder()
    {
        libraries = Maps.newHashMap();
        for (final SchemaVersion version: SchemaVersion.values())
            libraries.put(version.getLocation(), version.getLibrary());
    }

    ValidationConfigurationBuilder(final ValidationConfiguration cfg)
    {
        libraries = Maps.newHashMap(cfg.libraries);
        defaultLibrary = cfg.defaultLibrary;
    }

    ValidationConfigurationBuilder addLibrary(final String uri,
        final Library library)
    {
        final JsonRef ref = RefSanityChecks.absoluteRef(uri);
        if (library == null)
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(NULL_LIBRARY));
        if (libraries.containsKey(ref))
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(DUP_LIBRARY).put("uri", ref));
        libraries.put(ref, library);
        return this;
    }

    ValidationConfigurationBuilder setDefaultLibrary(final String uri,
        final Library library)
    {
        addLibrary(uri, library);
        defaultLibrary = library;
        return this;
    }

    @Override
    public ValidationConfiguration freeze()
    {
        return new ValidationConfiguration(this);
    }
}
