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

/**
 * JSON document downloaders
 *
 * <p>You will normally not use any of these classes directly. {@code http}
 * is for the moment the only scheme supported natively. You can expand the
 * set of supported schemes by:</p>
 *
 * <ul>
 *     <li>implementing {@link org.eel.kitchen.jsonschema.uri.URIDownloader},
 *     </li>
 *     <li>registering this new downloader using
 *     {@link org.eel.kitchen.jsonschema.main.JsonSchemaFactory.Builder#addURIDownloader(String, org.eel.kitchen.jsonschema.uri.URIDownloader)}.</li>
 * </ul>
 */
package org.eel.kitchen.jsonschema.uri;

import java.lang.String;