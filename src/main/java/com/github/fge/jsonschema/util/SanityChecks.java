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

package com.github.fge.jsonschema.util;

import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;

import java.net.URI;
import java.net.URISyntaxException;

import static com.github.fge.jsonschema.messages.LoadingMessages.*;

public final class SanityChecks
{
    private SanityChecks()
    {
    }

    public static JsonRef absoluteRef(final String input)
    {
        final URI uri;
        try {
            uri = new URI(input);
        } catch (URISyntaxException ignored) {
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(INVALID_URI));
        }
        final JsonRef ret = JsonRef.fromURI(uri);
        if (!ret.isAbsolute())
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(REF_NOT_ABSOLUTE));
        return ret;
    }

    public static URI absoluteLocator(final String input)
    {
        return absoluteRef(input).getLocator();
    }
}
