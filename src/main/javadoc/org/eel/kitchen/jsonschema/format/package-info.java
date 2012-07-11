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
 * <p>In theory, draft v3 also allows further format specifiers to be
 * registered, however this implementation does not support it.</p>
 *
 * <p>It should be noted that the specification is very clear that the
 * different specifiers refer to existing RFCs. In particular, for
 * {@code email} and {@code hostname}, this means that an email may not have
 * a right hand part at all, and that a hostname needs not be an FQDN. This
 * implementation strictly conforms to the specification!
 * </p>
 */
package org.eel.kitchen.jsonschema.format;