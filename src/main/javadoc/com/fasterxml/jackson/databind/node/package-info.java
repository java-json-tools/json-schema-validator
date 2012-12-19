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
 * Custom implementations for ArrayNode and ObjectNode
 *
 * <p>They are not {@code final} in Jackson. These implementations are final,
 * and perform better as a result due to code simplification (since the
 * underlying containers are also final, leading to very simple equals/hashCode
 * implementations).</p>
 */

package com.fasterxml.jackson.databind.node;
