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
