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
 * <p>This package contains all classes you will use to validate your
 * inputs:</p>
 *
 * <ul>
 *     <li>start by creating a {@link
 *     org.eel.kitchen.jsonschema.main.JsonSchemaFactory.Builder},</li>
 *     <li>customize it if need be,</li>
 *     <li>build your factory,</li>
 *     <li>register a schema,</li>
 *     <li>create a {@link org.eel.kitchen.jsonschema.main.JsonSchema} instance,
 *     </li>
 *     <li>validate your inputs.</li>
 * </ul>
 *
 * <p>{@link org.eel.kitchen.jsonschema.main.JsonSchema} is thread safe and
 * immutable, you can therefore use a same instance to validate as much data as
 * you want -- in parallel.</p>
 *
 * <p>You can customize your schema factory (via the builder) in several ways:
 * </p>
 *
 * <ul>
 *     <li>URI manipulations: setting a default namespace, setting redirections;
 *     </li>
 *     <li>keywords: registering/unregistering keywords, creating your own
 *     bundles, replacing/merging them;</li>
 *     <li>format specifiers: same as keywords;</li>
 *     <li>enabling validation features.</li>
 * </ul>
 *
 * <p>See also:</p>
 *
 * <ul>
 *     <li>{@link org.eel.kitchen.jsonschema.keyword},</li>
 *     <li>{@link org.eel.kitchen.jsonschema.format}.</li>
 * </ul>
 *
 */
package org.eel.kitchen.jsonschema.main;
