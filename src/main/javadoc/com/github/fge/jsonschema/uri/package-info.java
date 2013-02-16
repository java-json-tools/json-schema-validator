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
 * Classes to handle downloading of JSON documents using any URI scheme
 *
 * <p>The following URI schemes are supported by default: {@code http}, {@code
 * https}, {@code file}, {@code ftp}, {@code resource} and {@code jar}.</p>
 *
 * <p>You can use classes in this package to extend the list of supported
 * schemes, by:</p>
 *
 * <ul>
 *     <li>implementing {@link com.github.fge.jsonschema.load.URIDownloader},
 *     </li>
 *     <li>registering this new downloader for a custom scheme by using {@link
 *     com.github.fge.jsonschema.cfg.LoadingConfigurationBuilder#addScheme(
 *     java.lang.String, com.github.fge.jsonschema.load.URIDownloader)}.</li>
 * </ul>
 *
 * <p>Note about {@code jar} relative URI resolutions: due to the very nature of
 * {@code jar} URIs, normal URI resolving rules cannot be applied. This
 * implementation ensures that relative ref resolutions happen correctly, even
 * though this is a violation of the URI specification.</p>
 */
package com.github.fge.jsonschema.uri;
