/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Pattern;

/**
 * <p>ECMA 262 validation helper. Rhino is used instead of
 * {@link java.util.regex} because the latter doesn't comply with ECMA 262:</p>
 *
 * <ul>
 *     <li>ECMA 262 doesn't have {@link Pattern#DOTALL};</li>
 *     <li>ECMA 262 doesn't have "possessive" quantifiers ({@code ++},
 *     {@code ?+}, etc);</li>
 *     <li>there is only one word delimiter in ECMA 262, which is {@code \b};
 *     {@code \&lt;} (for beginning of word) and {@code \&gt;} (for end
 *     of word) are not understood.
 *     </li>
 * </ul>
 *
 * <p>And many, many other things. See
 * <a href="http://www.regular-expressions.info/javascript.html">here</a>
 * for the full story. And if you don't yet have Jeffrey Friedl's "Mastering
 * regular expressions", just <a href="http://regex.info">buy it</a> :p</p>
 */
public final class RhinoHelper
{
    /**
     * The scriptlet template used to validate a regex
     */
    private static final String REGEX_FORMAT = "/%s/";

    /**
     * The scriptlet template used to match an input against a regex
     */
    private static final String REGEX_VALIDATE = "/%s/.test(\"%s\")";

    /**
     * The {@link Pattern} used to recognize a /, using a positive lookahead
     * construct
     */
    private static final Pattern SLASH_LOOKAHEAD = Pattern.compile("(?=/)");

    /**
     * Instance of Rhino's {@link ScriptEngine} to use.
     */
    private static final ScriptEngine engine
        = new ScriptEngineManager().getEngineByName("JavaScript");

    /**
     * Validate that a regex is correct
     *
     * @param regex the regex to validate
     * @return true if the regex is valid
     */
    public static boolean regexIsValid(final String regex)
    {
        try {
            engine.eval(String.format(REGEX_FORMAT, escape(regex)));
            return true;
        } catch (ScriptException ignored) {
            return false;
        }
    }

    /**
     * <p>Matches an input against a given regex, in the <b>real</b> sense
     * of matching, that is, the regex can match anywhere in the input.
     * Java's {@link java.util.regex} makes the unfortunate mistake to make
     * people believe that matching is done on the whole input... Which is
     * not true.
     * </p>
     *
     * <p>Also note that the regex MUST have been validated at this point
     * (using {@link #regexIsValid(String)}).</p>
     *
     * @param regex the regex to use
     * @param input the input to match against (and again, see description)
     * @return true if the regex matches the input
     */
    public static boolean regMatch(final String regex, final String input)
    {
        final String js = String.format(REGEX_VALIDATE, escape(regex), input);

        try {
            return (Boolean) engine.eval(js);
        } catch (ScriptException e) {
            throw new RuntimeException("Should never have reached this point!"
                + " Regex SHOULD have been validated already", e);
        }
    }

    /**
     * Utility function to escape slashes in a given regex string. Given that we
     * feed the regex to Rhino as {@code /re/}, where {@code re} is the regex,
     * we need to prepend a backslash to all slashes found in {@code re}.
     *
     * @param regex the regex as a String
     * @return the regex, with all slashes escaped
     */
    private static String escape(final String regex)
    {
        return SLASH_LOOKAHEAD.matcher(regex).replaceAll("\\\\");
    }
}
