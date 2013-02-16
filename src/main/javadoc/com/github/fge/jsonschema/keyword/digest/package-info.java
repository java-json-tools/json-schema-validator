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
 * Keyword digesters
 *
 * <p>Digesters play two important roles:</p>
 *
 * <ul>
 *     <li>they detect similar schemas for a given keyword, and produce the same
 *     output in this case;</li>
 *     <li>they provide to later elements in the validation chain the list of
 *     types validated by a keyword.</li>
 * </ul>
 *
 * <p>The first item has two advantages: not only is the library's memory
 * footprint reduced, it also allows a great simplification of keyword
 * constructors.</p>
 */

package com.github.fge.jsonschema.keyword.digest;
