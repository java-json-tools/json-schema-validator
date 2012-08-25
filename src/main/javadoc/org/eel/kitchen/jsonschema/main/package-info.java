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
 *     org.eel.kitchen.jsonschema.main.JsonSchemaFactory},</li>
 *     <li>customize it if need be (by adding new keywords,
 *     removing existing ones, etc),</li>
 *     <li>register a schema,</li>
 *     <li>create a schema instance,</li>
 *     <li>validate your inputs.</li>
 * </ul>
 *
 * <p>A single {@link org.eel.kitchen.jsonschema.main.JsonSchema} instance can
 * validate as many inputs as required. What is more, it is fully thread
 * safe.</p>
 */
package org.eel.kitchen.jsonschema.main;
