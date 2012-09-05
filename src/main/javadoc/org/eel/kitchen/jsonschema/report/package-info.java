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
 * Validation reports and messages
 *
 * <p>All validation messages use the {@link
 * org.eel.kitchen.jsonschema.report.Message} class, which can itself be
 * represented as JSON.</p>
 *
 * <p>Validation reports are maps of messages, with keys to the map being
 * pointers into the instance being validated, and values being arrays of
 * validation messages for that path. A validation report also has JSON
 * representations (one as an array, another as an object).</p>
 */
package org.eel.kitchen.jsonschema.report;
