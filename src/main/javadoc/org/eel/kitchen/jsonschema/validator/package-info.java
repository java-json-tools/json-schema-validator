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
 * Core validation process
 *
 * <p>You will normally never use these classes directly. They are spawned  by
 * {@link org.eel.kitchen.jsonschema.main.JsonSchema} instances to proceed
 * to the validation itself.</p>
 *
 * <p>The order is always the same:</p>
 *
 * <ul>
 *     <li>{@link org.eel.kitchen.jsonschema.validator.RefResolverJsonValidator},
 *     </li>
 *     <li>{@link org.eel.kitchen.jsonschema.validator.SyntaxJsonValidator},
 *     </li>
 *     <li>{@link org.eel.kitchen.jsonschema.validator.InstanceJsonValidator},
 *     </li>
 *     <li>and then either of a
 *     {@link org.eel.kitchen.jsonschema.validator.ArrayJsonValidator} or a
 *     {@link org.eel.kitchen.jsonschema.validator.ObjectJsonValidator}
 *     if the instance is either an array or an object (validation stops
 *     otherwise).</li>
 * </ul>
 *
 */
package org.eel.kitchen.jsonschema.validator.;
