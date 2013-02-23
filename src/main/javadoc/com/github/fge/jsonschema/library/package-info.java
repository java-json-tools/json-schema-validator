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
 * Schema keyword libraries
 *
 * <p>A {@link com.github.fge.jsonschema.library.Library} contains a set of
 * keywords and all elements related to them (syntax validators, digesters and
 * validator classes), along with format attributes.</p>
 *
 * <p>Libraries are immutable, but you can obtain a thawed copy of them in
 * which you can inject new keywords and format attributes (or remove existing
 * ones).</p>
 *
 * <p>The two predefined libraries are the draft v4 core schema library and the
 * draft v3 core schema library.</p>
 */
package com.github.fge.jsonschema.library;
