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
 * <p>All format specifiers (section 5.23 of draft v3) are supported,
 * except for {@code style} and {@code color}.</p>
 *
 * <p>It should be noted that this implementation differs from the draft in
 * one subtle, but important way: strictly speaking, an email and a hostname
 * may have no domain part at all. However, this implementation chooses to
 * require that they have one by default. You can however ask the library to
 * obey the RFCs to the letter (see {@link
 * org.eel.kitchen.jsonschema.main.ValidationFeature#STRICT_RFC_CONFORMANCE}).
 * </p>
 *
 * <p>This implementation also adds a custom format specifier,
 * {@code date-time-ms}: this is the same as ISO 8601's {@code date-time},
 * with added milliseconds.</p>
 *
 * <p>Note: in theory, draft v3 also allows further format specifiers to be
 * registered, however this implementation does not support it currently.</p>
 */
package org.eel.kitchen.jsonschema.format;