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

/**
 * Core validation processors
 *
 * <p>A full validation makes use of the following individual processors, in
 * order:</p>
 *
 * <ul>
 *     <li>{@link com.github.fge.jsonschema.processors.ref reference
 *     resolution};</li>
 *     <li>{@link com.github.fge.jsonschema.processors.syntax syntax checking};
 *     </li>
 *     <li>{@link com.github.fge.jsonschema.processors.digest schema digesting};
 *     </li>
 *     <li>{@link com.github.fge.jsonschema.processors.build keyword building}.
 *     </li>
 * </ul>
 *
 * <p>All these individual processors are wrapped into a {@link
 * com.github.fge.jsonschema.processors.validation.ValidationProcessor}, which
 * handles the validation process as a whole -- including {@code $schema}
 * detection.</p>
 */
package com.github.fge.jsonschema.processors;
