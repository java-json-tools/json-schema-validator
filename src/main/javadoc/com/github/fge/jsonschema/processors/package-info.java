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
 * Core validation processors
 *
 * <p>A full validation makes use of the following individual processors, in
 * order:</p>
 *
 * <ul>
 *     <li>{@link com.github.fge.jsonschema.processors.ref reference
 *     resolution};</li>
 *     <li>{@link com.github.fge.jsonschema.processors.syntax syntax checking};
 *     </li>
 *     <li>{@link com.github.fge.jsonschema.processors.digest schema digesting};
 *     </li>
 *     <li>{@link com.github.fge.jsonschema.processors.build keyword building}.
 *     </li>
 * </ul>
 *
 * <p>All these individual processors are wrapped into a {@link
 * com.github.fge.jsonschema.processors.validation.ValidationProcessor}, which
 * handles the validation process as a whole -- including {@code $schema}
 * detection.</p>
 */
package com.github.fge.jsonschema.processors;
