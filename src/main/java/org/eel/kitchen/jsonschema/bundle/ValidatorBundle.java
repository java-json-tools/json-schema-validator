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

package org.eel.kitchen.jsonschema.bundle;

import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;

import java.util.Map;

public interface ValidatorBundle
{
    /**
     * Return the list of registered syntax validators
     *
     * @return a map pairing keywords to their validators
     */
    Map<String, SyntaxValidator> syntaxValidators();

    /**
     * Return the list of registered keyword validators and associated
     * instance types
     *
     * @return a map pairing instance types and keywords to validators
     */
    Map<NodeType, Map<String, KeywordValidator>> keywordValidators();

    /**
     * Register a validator for a given keyword
     *
     * @param keyword the schema keyword
     * @param sv the syntax validator
     * @param kv the keyword validator
     * @param types the list of JSON node types this keyword applies to
     */
    void registerValidator(final String keyword, final SyntaxValidator sv,
        final KeywordValidator kv, final NodeType... types);

    /**
     * Unregister a validator for a given keyword
     *
     * <p>Please note that an unknown keyword will yield an error at syntax
     * validation level.
     * </p>
     *
     * @param keyword the victim
     */
    void unregisterValidator(final String keyword);
}
