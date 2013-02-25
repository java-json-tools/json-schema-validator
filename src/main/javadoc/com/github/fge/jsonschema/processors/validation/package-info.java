/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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
 * Main validation processors and utility classes
 *
 * <p>The main processors are:</p>
 *
 * <ul>
 *     <li>{@link
 *     com.github.fge.jsonschema.processors.validation.ValidationProcessor},
 *     which is the backbone behind all main classes, and the coordinator of
 *     all individual validation processors;</li>
 *     <li>{@link
 *     com.github.fge.jsonschema.processors.validation.ValidationChain}, which
 *     performs the schema-to-keyword conversion.</li>
 * </ul>
 */
package com.github.fge.jsonschema.processors.validation;
