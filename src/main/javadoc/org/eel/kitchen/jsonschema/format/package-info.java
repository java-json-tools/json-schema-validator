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
 * Format attribute classes
 *
 * <p>The {@code format} keyword is defined by section 5.23 of the current
 * draft. This particular package supports the following format attributes:</p>
 *
 * <ul>
 *     <li>{@code date-time};</li>
 *     <li>{@code email};</li>
 *     <li>{@code host-name};</li>
 *     <li>{@code ip-address};</li>
 *     <li>{@code ipv6};</li>
 *     <li>{@code regex};</li>
 *     <li>{@code uri}.</li>
 * </ul>
 *
 * <p>All other format attributes defined by the draft (except {@code color} and
 * {@code style}, for which support is deliberately omitted) are now in a
 * separate package: <a
 * href="https://github.com/fge/json-schema-formats">json-schema-formats
 * </a>.</p>
 *
 */
package org.eel.kitchen.jsonschema.format;