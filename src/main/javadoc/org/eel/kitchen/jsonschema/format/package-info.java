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
 * Format specifier classes
 *
 * <p>The {@code format} keyword is defined by section 5.23 of the current
 * draft. This particular package supports the following format specifiers:</p>
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
 * <p>All other format specifiers defined by the draft (except {@code color} and
 * {@code style}, for which support is deliberately omitted) are now in a
 * separate package: <a
 * href="https://github.com/json-schema/json-schema-formats">json-schema-formats
 * </a>.</p>
 *
 * <p>In addition to using the package above, you can also implement your own
 * format specifiers by creating a {@link
 * org.eel.kitchen.jsonschema.format.FormatBundle} and setting/merging your
 * bundle into your schema factory. See also {@link
 * org.eel.kitchen.jsonschema.format.FormatSpecifier}.</p>
 *
 * <p>Note: by default, the {@code host-name} and {@code email} format
 * specifiers require that both validated values have a domain part. This
 * contradicts what the respective RFCs say, but it is more in line with user
 * expectations. You can tell these formats to strictly comply to the RFC by
 * setting {@link
 * org.eel.kitchen.jsonschema.main.ValidationFeature#STRICT_RFC_CONFORMANCE}
 * when building your schema factory (see {@link
 * org.eel.kitchen.jsonschema.main.JsonSchemaFactory.Builder#enableFeature(org.eel.kitchen.jsonschema.main.ValidationFeature)}).
 * </p>
 *
 */
package org.eel.kitchen.jsonschema.format;