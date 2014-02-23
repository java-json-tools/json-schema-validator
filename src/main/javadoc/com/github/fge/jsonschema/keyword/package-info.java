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
 * Keyword syntax checkers, digesters and validators
 *
 * <p>One schema keyword consists, apart from its name, of three elements:</p>
 *
 * <ul>
 *     <li>a {@link com.github.fge.jsonschema.keyword.syntax.SyntaxChecker}
 *     which checks whether the keyword has the correct syntax in a JSON schema;
 *     </li>
 *     <li>a {@link com.github.fge.jsonschema.keyword.digest.Digester} which
 *     digests the schema for a specific keyword so as to use a more efficient
 *     form when building the validator;</li>
 *     <li>a {@link
 *     com.github.fge.jsonschema.keyword.validator.KeywordValidator} which
 *     validates an instance against the schema.</li>
 * </ul>
 *
 * <p>All these elements are run in this order. Note that keyword validators
 * are always built by reflection. You can create your own custom keyword by
 * providing its name and these three elements wrapped into a {@link
 * com.github.fge.jsonschema.library.Keyword} to inject into a {@link
 * com.github.fge.jsonschema.library.Library}, which you will then submit to a
 * {@link com.github.fge.jsonschema.cfg.ValidationConfiguration}.</p>
 */

package com.github.fge.jsonschema.keyword;
