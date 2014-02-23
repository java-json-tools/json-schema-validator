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
 * Schema keyword libraries
 *
 * <p>A {@link com.github.fge.jsonschema.library.Library} contains a set of
 * keywords and all elements related to them (syntax validators, digesters and
 * validator classes), along with format attributes.</p>
 *
 * <p>Libraries are immutable, but you can obtain a thawed copy of them in
 * which you can inject new keywords and format attributes (or remove existing
 * ones).</p>
 *
 * <p>The two predefined libraries are the draft v4 core schema library and the
 * draft v3 core schema library.</p>
 */
package com.github.fge.jsonschema.library;
