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
 * <p>There are two utility classes.</p>
 *
 * <p>{@link com.github.fge.jsonschema.util.JacksonUtils} is a
 * utility class with methods to return an appropriate {@link
 * com.fasterxml.jackson.databind.node.JsonNodeFactory} and {@link
 * com.fasterxml.jackson.databind.ObjectMapper} for the needs of JSON Schema.
 * While the first is not that important, the second ensures that decimal
 * number nodes are read using {@link java.math.BigDecimal} instead of {@code
 * double}, so that there is no precision loss for numeric comparisons
 * (especially important for divisibility tests). It also contains a method
 * for returning an empty object, and another one to render an {@link
 * com.fasterxml.jackson.databind.node.ObjectNode} as a {@link java.util.Map}.
 * </p>
 *
 * <p>{@link com.github.fge.jsonschema.util.equivalence.JsonSchemaEquivalence} is an
 * implementation of Guava's {@link com.google.common.base.Equivalence} for
 * {@link com.fasterxml.jackson.databind.JsonNode} instances. It exists solely
 * for the purpose of conforming to the JSON Schema definition of equality,
 * which requires that two numeric instances are equal if their mathematical
 * value is the same (that is, {@code 1.0} and {@code 1} are the same). This is
 * not the case by default with Jackson.</p>
 */
package com.github.fge.jsonschema.util.jackson;

