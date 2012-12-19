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
 * Various utility classes
 *
 * <p>There are five utility classes, all of which are important.</p>
 *
 * <p>The two most important of them are {@link
 * org.eel.kitchen.jsonschema.util.jackson.NumberNode} and {@link
 * org.eel.kitchen.jsonschema.util.jackson.CustomJsonNodeFactory}. They help enforce
 * numeric value equality as defined by JSON Schema, since for instance, Jackson
 * does not consider {@code 1.0} and {@code 1} as being equal. This
 * implementation does consider them as equal, all the while retaining the node
 * type.</p>
 *
 * <p>{@link org.eel.kitchen.jsonschema.util.JsonLoader} contains various
 * methods to load JSON documents as {@link
 * com.fasterxml.jackson.databind.JsonNode} (schemas as well as instances to
 * validate).</p>
 *
 * <p>{@link org.eel.kitchen.jsonschema.util.RhinoHelper} is in charge of all
 * regex validation: as the standard dictates ECMA 262 regexes, using {@link
 * java.util.regex} is out of the question. See this class' description for more
 * details.</p>
 *
 * <p>The {@link org.eel.kitchen.jsonschema.util.NodeType} enum is a critical
 * part of the code. Its ability to determine the type of a {@link
 * com.fasterxml.jackson.databind.JsonNode} is an essential part of the
 * validation process.</p>
 *
 * <p>Finally, {@link org.eel.kitchen.jsonschema.util.CharMatchers} contains a
 * set of custom {@link com.google.common.base.CharMatcher}s for hyper schema
 * syntax validation.</p>
 */
package org.eel.kitchen.jsonschema.util;
