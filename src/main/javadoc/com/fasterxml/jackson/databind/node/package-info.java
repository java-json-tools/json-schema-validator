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
 * Override of Jackson's JsonNodeFactory, as of version 2.0.6
 *
 * <p>This whole package is bound to disappear when both problems are
 * solved:</p>
 *
 * <ul>
 *     <li>{@link com.fasterxml.jackson.databind.node.ObjectNode} is buggy in
 *     2.1.0 (does not build properly from an empty map; see <a
 *     href="https://github.com/FasterXML/jackson-databind/issues/80">here</a>);
 *     </li>
 *     <li>{@link com.fasterxml.jackson.databind.node.JsonNodeFactory} does not
 *     build numeric nodes "properly" when using {@link java.math.BigDecimal}
 *     in versions less than 2.1.x (see <a
 *     href="https://github.com/FasterXML/jackson-databind/issues/93">here</a>.
 *     </li>
 * </ul>
 *
 * <p>As a result, this project relies on an "old" version of Jackson (2.0.6)
 * and overrides JsonNodeFactory to account for the first bug.</p>
 */
package com.fasterxml.jackson.databind.node;
