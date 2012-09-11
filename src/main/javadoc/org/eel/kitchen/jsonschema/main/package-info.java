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
 * <p>You can also customize your schema factory (via the builder) in several
 * ways:</p>
 *
 * <ul>
 *     <li>URI manipulations: setting a default namespace, setting redirections,
 *     registering downloaders for arbitrary URI schemes;
 *     </li>
 *     <li>keywords: registering/unregistering keywords, creating your own
 *     bundles, replacing/merging them;</li>
 *     <li>format attributes: same as keywords;</li>
 *     <li>enabling validation features.</li>
 * </ul>
 *
 * <p>See {@link org.eel.kitchen.jsonschema.main.JsonSchemaFactory.Builder}.</p>
 *
 */
package org.eel.kitchen.jsonschema.main;
