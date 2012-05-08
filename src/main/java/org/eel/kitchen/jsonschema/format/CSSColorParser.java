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

package org.eel.kitchen.jsonschema.format;

import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Parboiled grammar to match a CSS 2.1 color definitions
 *
 * The <a href="http://www.w3.org/TR/CSS2/syndata.html#color-units">W3C</a>
 * says that a color can be either of:
 */
@BuildParseTree
public class CSSColorParser
    extends RepeatJoin
{
    /**
     * The 17 color names defined by CSS 2.1
     */
    private static final String[] colorNames = {
        "maroon", "red", "orange", "yellow", "olive", "green", "purple",
        "fuchsia", "lime", "teal", "aqua", "blue", "navy", "black", "gray",
        "silver", "white"
    };

    /**
     * The 17 rules matching color names, case insensitive
     */
    private final Object[] COLORS;

    /**
     * Constructor
     *
     * Initializes the case insensitive color matchings.
     */
    public CSSColorParser()
    {
        final List<Rule> rules = new ArrayList<Rule>(colorNames.length);

        for (final String color: colorNames)
            rules.add(IgnoreCase(color));

        COLORS = rules.toArray();
    }

    /**
     * When matching an integer, check that it is bounded by two values
     *
     * @param start the lower bound
     * @param end the upper bould
     * @return the action
     */
    static <V> Action<V> RangeCheck(final int start, final int end)
    {
        if (start > end)
            throw new IllegalStateException();

        return new Action<V>() {

            /**
             * Runs the parser action.
             *
             * @param context the current parsing context
             * @return true if the parsing process is to proceed,
             * false if the current rule is to fail
             */
            @Override
            public boolean run(final Context<V> context)
            {
                final int value;

                try {
                    value = Integer.parseInt(context.getMatch());
                } catch (NumberFormatException ignored) {
                    //Overflow
                    return false;
                }
                return value >= start && value <= end;
            }
        };
    }

    /**
     * Match a positive integer bounded by two values
     *
     * @param start the lower bound
     * @param end the upper bound
     * @return the matching rule
     */
    Rule PositiveInteger(final int start, final int end)
    {
        return Sequence(OneOrMore(Digit()), RangeCheck(start, end));
    }

    /**
     * Rule to match all digits between 0 and 9
     *
     * @return the matching rule
     */
    Rule Digit()
    {
        return CharRange('0', '9');
    }

    /**
     * Rule to match one of the 17 color names defined by CSS 2.1, case
     * insensitive
     *
     * @return the matching rule
     */
    Rule ColorByName()
    {
        return FirstOf(COLORS);
    }

    /**
     * Rule to match a hexadecimal digit, case insensitive
     *
     * @return the matching rule
     */
    Rule HexDigit()
    {
        return FirstOf(CharRange('a', 'f'), CharRange('A', 'F'), Digit());
    }

    /**
     * Rule to match 3 hexadecimal digits in a row
     *
     * @return the matching rule
     */
    Rule ThreeHexDigits()
    {
        return Repeat(3, HexDigit());
    }

    /**
     * Rule to match a "hexadecimal defined" CSS color
     *
     * <p>CSS colors defined this way can be either of {@code #xxx} or
     * {@code #xxxxxx}, where {@code x} are hexadecimal digits.</p>
     *
     * @return the matching rule
     */
    Rule CSSHexColor()
    {
        return Sequence('#', ThreeHexDigits(), Optional(ThreeHexDigits()));
    }

    /**
     * Rule to match all spacing characters allowed by CSS
     *
     * <p>They are exactly the same as, for instance, JSON,
     * and are matched by the character class {@code [ \r\n\t\f]}.</p>
     *
     * @return the matching rule
     */
    Rule Spaces()
    {
        return ZeroOrMore(AnyOf(" \r\n\t\f"));
    }

    /**
     * Rule to match a comma surrounded by spaces
     *
     * @return the matching rule
     */
    Rule Comma()
    {
        return Join(2, Spaces(), ',');
    }

    /**
     * Rule to match a CSS percent defined color element in an RGB definition
     *
     * <p>While the CSS standard theoretically allows for any number,
     * we only allow 0 to 100.</p>
     *
     * @return the matching rule
     */
    Rule PercentColorElement()
    {
        return Sequence(PositiveInteger(0, 100), '%');
    }

    /**
     * Rule to match a CSS number defined color element in an RGB definition
     *
     * <p>The same goes here as for {@link #PercentColorElement()}: only
     * values from 0 to 255 are allowed, but theoretically they can be any
     * integer.</p>
     *
     * @return the matching rule
     */
    Rule NumericColorElement()
    {
        return PositiveInteger(0, 255);
    }

    /**
     * Rule to match an RGB color definition as allowed by CSS 2.1
     *
     * <p>RGB defined colors have the shape {@code rgb(x, y, z)} where elements
     * are either percentage defined or number defined, but never a mix of both.
     *
     * @return the matching rule
     */
    Rule CSSRGBColor()
    {
        return Sequence(
            "rgb(",
            Spaces(),
            FirstOf(
                Join(3, PercentColorElement(), Comma()),
                Join(3, NumericColorElement(), Comma())
            ),
            Spaces(),
            ')'
        );
    }

    /**
     * Rule to match any CSS defined color (by name, hexadecimal or RGB)
     *
     * @return the matching rule
     */
    public Rule CSSColor()
    {
        return Sequence(FirstOf(ColorByName(), CSSHexColor(), CSSRGBColor()),
            EOI);
    }
}
