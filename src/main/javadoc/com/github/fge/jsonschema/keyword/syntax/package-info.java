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
 * Keyword syntax checkers
 *
 * <p>Syntax checkers play a critical role in the validation process. If syntax
 * validation checking fails, the validation process stops, and syntax
 * validation depends on these checkers.</p>
 *
 * <p>A successful syntax validation ensures that digesters and validators will
 * not see malformed inputs, and as such they do not even need to care whether
 * their input is valid -- they know that it is.</p>
 *
 * <p>Even though you can turn it off, it is not recommended. Take this schema
 * as an example:</p>
 *
 * <pre>
 *     {
 *         "$ref": "#/properties",
 *         "properties": {
 *             "type": { "type": "string" }
 *         }
 *     }
 * </pre>
 *
 * <p>This schema is syntactically valid; however, if someone tries and
 * validates against this schema, the JSON Reference leads to a schema which is
 * <b>not</b> valid; syntax checking will detect this since it takes place
 * right after JSON Reference processing. If there were no syntax checking, the
 * matching digester for the {@code type} keyword would throw a {@link
 * java.lang.NullPointerException}.</p>
 */

package com.github.fge.jsonschema.keyword.syntax;
