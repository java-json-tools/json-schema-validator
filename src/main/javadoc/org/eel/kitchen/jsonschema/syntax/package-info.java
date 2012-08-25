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
 * Schema syntax validation package
 *
 * <p>The main class in this package is {@link
 * org.eel.kitchen.jsonschema.syntax.SyntaxValidator}.</p>
 *
 * <p>Syntax validation has a critically important role in the validation
 * process. An invalid schema will always <i>fail</i> to invalidate a JSON
 * instance.</p>
 *
 * <p>For this implementation in particular, it also helps to ensure that the
 * {@link org.eel.kitchen.jsonschema.keyword.KeywordValidator} associated with
 * the schema keyword does not need to preoccupy about its arguments being
 * well-formed -- they will be since the syntax validator has verified that they
 * are.</p>
 *
 * <p>Unlike keyword validators, syntax validators are not built by reflection.
 * It is therefore your responsibility to instantiate it and only then register
 * it.</p>
 */
package org.eel.kitchen.jsonschema.syntax;
