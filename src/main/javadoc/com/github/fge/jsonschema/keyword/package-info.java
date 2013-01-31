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
 * Schema validation core elements: keyword validators
 *
 * <p>All keyword validators are built via reflection, since they are dependent
 * on the schema being passed as an argument. Therefore, if you create a
 * keyword validator of yours, you <b>must</b> provide a constructor with a
 * single argument of type {@link com.fasterxml.jackson.databind.JsonNode}.</p>
 *
 * <p>Not only this, but if you do, be sure to pair it with a {@link
 * com.github.fge.jsonschema.old.syntax.SyntaxChecker}. The principle is that the
 * syntax checker checks that the keyword has a correct shape, which means the
 * keyword validator does not have to check for this. This considerably
 * simplifies the constructor (you only have to do minimal type checking, if
 * any).</p>
 *
 * <p>To create a new validator, you may either extend {@link
 * com.github.fge.jsonschema.keyword.KeywordValidator} or one of these two
 * specific subclasses:</p>
 *
 * <ul>
 *     <li>{@link com.github.fge.jsonschema.keyword.NumericKeywordValidator}
 *     (used for numeric instance validation),</li>
 *     <li>{@link
 *     com.github.fge.jsonschema.keyword.PositiveIntegerKeywordValidator} (used
 *     for keywords which take a positive integer as an argument).</li>
 * </ul>
 */
package com.github.fge.jsonschema.keyword;
