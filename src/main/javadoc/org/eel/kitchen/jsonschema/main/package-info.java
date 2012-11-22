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
 * Main schema validation API
 *
 * <p>This package contains all classes you need to validate your data.</p>
 *
 * <p>Start by building a {@link
 * org.eel.kitchen.jsonschema.main.JsonSchemaFactory}, then use it to build
 * {@link org.eel.kitchen.jsonschema.main.JsonSchema} instances (one per
 * schema).</p>
 *
 * <p>{@link org.eel.kitchen.jsonschema.main.JsonSchema} is thread-safe and
 * immutable (<i>and</i> concurrent-friendly), you can therefore use a single
 * instance to validate any amount of data. Typically, if you use a single
 * schema, you can make that one instante {@code private static final}.</p>
 *
 * <p>One important thing to remember is that {@code JsonSchema} also does
 * syntax validation, and that syntax validation is done on demand. The reasons
 * for this is as follows:</p>
 *
 * <ul>
 *     <li>recursive schema validation would also require that JSON references
 *     be processed, resolved, and recursively checked as well;</li>
 *     <li>some parts of a schema may not be used at all during instance
 *     validation.</li>
 * </ul>
 *
 * <p>You can also customize your schema factory (via the builder) in several
 * ways. See {@link org.eel.kitchen.jsonschema.examples}.</p>
 *
 */
package org.eel.kitchen.jsonschema.main;
