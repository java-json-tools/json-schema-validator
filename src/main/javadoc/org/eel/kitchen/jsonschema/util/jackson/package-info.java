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
 * Utility classes for Jackson
 *
 * <p>There are five utility classes:</p>
 *
 * <ul>
 *     <li>{@link org.eel.kitchen.jsonschema.util.jackson.NumberNode} is a
 *     wrapper class over {@link
 *     com.fasterxml.jackson.databind.node.NumericNode} which respects JSON
 *     Schema's definition of numeric equality (ie, {@code 1.0} is equal to
 *     {@code 1}), all the while retaining type detection (ie, {@code 1.0} is
 *     not an integer);</li>
 *     <li>{@link org.eel.kitchen.jsonschema.util.jackson.JsonArray} and {@link
 *     org.eel.kitchen.jsonschema.util.jackson.JsonObject} are implementations
 *     overriding Jackson's {@link
 *     com.fasterxml.jackson.databind.node.ArrayNode} and {@link
 *     com.fasterxml.jackson.databind.node.ObjectNode} respectively;
 *     <li>{@link org.eel.kitchen.jsonschema.util.jackson.CustomJsonNodeFactory}
 *     is a custom implementation of {@link
 *     com.fasterxml.jackson.databind.node.JsonNodeFactory} which generates
 *     numeric nodes using the class above, but also this package's overriden
 *     {@link com.fasterxml.jackson.databind.node.ArrayNode} and {@link
 *     com.fasterxml.jackson.databind.node.ObjectNode};</li>
 *     <li>{@link org.eel.kitchen.jsonschema.util.jackson.JacksonUtils} contains
 *     a single method generating a {@link java.util.Map} out of an object's
 *     members.</li>
 * </ul>
 *
 * <p>The custom node factory also provides a custom {@link
 * com.fasterxml.jackson.databind.ObjectMapper}, since we always deserialize
 * floating point numbers using {@link java.math.BigDecimal}, which is not the
 * default.</p>
 */
package org.eel.kitchen.jsonschema.util.jackson;

import java.lang.EnumConstantNotPresentException;