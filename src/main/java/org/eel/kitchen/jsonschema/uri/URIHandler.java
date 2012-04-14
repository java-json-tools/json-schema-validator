/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.uri;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URI;

/**
 * Interface which URI handlers must implement in order to provide support
 * for arbitrary URI schemes
 */
public interface URIHandler
{
    /**
     * Get the JSON document located at a given URI
     *
     * @param uri the URI
     * @return the document at this URI
     * @throws IOException the document could not be downloaded
     */
    JsonNode getDocument(final URI uri)
        throws IOException;
}
