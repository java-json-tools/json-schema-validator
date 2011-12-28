/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.util;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for CSS Parboiled grammars
 *
 */
@BuildParseTree
public class RepeatJoin
    extends BaseParser<Object>
{
    /**
     * Match a rule a defined number of times
     *
     * @param repetitions the number of repetitions of the rule
     * @param rule the base rule
     * @return the resulting rule (using {@link BaseParser#Sequence(Object[])})
     * @throws IllegalStateException the number of repetitions is less than
     * or equal to 0
     */
    Rule Repeat(final int repetitions, final Object rule)
    {
        if (repetitions <= 0)
            throw new IllegalStateException();

        if (repetitions == 1)
            return toRule(rule);

        final List<Object> rules = new ArrayList<Object>(repetitions);
        for (int i = 0; i < repetitions; i++)
            rules.add(rule);

        return Sequence(rules.toArray());
    }

    /**
     * Build a rule based on a given rule and a separator
     *
     * <p>For instance, for 3 repetitions of a rule {@code r} separated by
     * separator {@code s}, this will build a rule matching:</p>
     * <pre>
     *     r, s, r, s, r
     * </pre>
     *
     * @param repetitions the number of repetitions
     * @param rule the rule
     * @param separator the separator to insert
     * @return the resulting rule (using {@link BaseParser#Sequence(Object[])})
     * @throws IllegalStateException the number of repetitions is less than
     * or equal to 0
     */
    Rule Join(final int repetitions, final Object rule, final Object separator)
    {
        if (repetitions <= 0)
            throw new IllegalStateException();

        if (repetitions == 1)
            return toRule(rule);

        final List<Object> rules = new ArrayList<Object>(repetitions * 2 - 1);
        rules.add(rule);
        for (int i = 1; i < repetitions; i++) {
            rules.add(separator);
            rules.add(rule);
        }

        return Sequence(rules.toArray());
    }
}
