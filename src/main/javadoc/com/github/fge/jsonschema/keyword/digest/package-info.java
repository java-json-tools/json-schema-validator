/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

/**
 * Keyword digesters
 *
 * <p>Digesters play two important roles:</p>
 *
 * <ul>
 *     <li>they detect similar schemas for a given keyword, and produce the same
 *     output in this case;</li>
 *     <li>they provide to later elements in the validation chain the list of
 *     types validated by a keyword.</li>
 * </ul>
 *
 * <p>The first item has two advantages: not only is the library's memory
 * footprint reduced, it also allows a great simplification of keyword
 * constructors.</p>
 */

package com.github.fge.jsonschema.keyword.digest;
