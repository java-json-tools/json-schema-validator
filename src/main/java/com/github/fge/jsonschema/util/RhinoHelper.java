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

package com.github.fge.jsonschema.util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.util.regex.Pattern;

/**
 * <p>ECMA 262 validation helper. Rhino is used instead of {@link
 * java.util.regex} because the latter doesn't comply with ECMA 262:</p>
 *
 * <ul>
 *     <li>ECMA 262 doesn't have {@link Pattern#DOTALL};</li>
 *     <li>ECMA 262 doesn't have "possessive" quantifiers ({@code ++},
 *     {@code ?+}, etc);</li>
 *     <li>there is only one word delimiter in ECMA 262, which is {@code \b};
 *     {@code \<} (for beginning of word) and {@code \>} (for end of word) are
 *     not understood.</li>
 * </ul>
 *
 * <p>And many, many other things. See
 * <a href="http://www.regular-expressions.info/javascript.html">here</a> for
 * the full story. And if you don't yet have Jeffrey Friedl's "Mastering regular
 * expressions", just <a href="http://regex.info">buy it</a> :p</p>
 */
public final class RhinoHelper
{
    /**
     * JavaScript scriptlet defining functions {@link #regexIsValid}
     * and {@link #regMatch}
     */
    private static final String jsAsString
        = "function regexIsValid(re)"
        + '{'
        + "    try {"
        + "         new RegExp(re);"
        + "         return true;"
        + "    } catch (e) {"
        + "        return false;"
        + "    }"
        + '}'
        + ""
        + "function regMatch(re, input)"
        + '{'
        + "    return new RegExp(re).test(input);"
        + '}';

    /**
     * Script context to use
     */
    private static final Context ctx;

    /**
     * Script scope
     */
    private static final Scriptable sharedScope;

    /**
     * Reference to Javascript function for regex validation
     */
    private static final Function regexIsValid;

    /**
     * Reference to Javascript function for regex matching
     */
    private static final Function regMatch;

    private RhinoHelper()
    {
    }

    static {
        ctx = Context.enter();
        sharedScope = ctx.initStandardObjects();
        ctx.evaluateString(sharedScope, jsAsString, "re", 1, null);
        regexIsValid = (Function) sharedScope.get("regexIsValid", sharedScope);
        regMatch = (Function) sharedScope.get("regMatch", sharedScope);
        ctx.seal(null);
    }

    /**
     * Validate that a regex is correct
     *
     * @param regex the regex to validate
     * @return true if the regex is valid
     */
    public static boolean regexIsValid(final String regex)
    {
        final Context context = Context.enter();
        try {
            final Scriptable scope = context.newObject(sharedScope);
            scope.setPrototype(sharedScope);
            scope.setParentScope(null);
            return (Boolean) regexIsValid.call(context, scope, scope,
                new Object[]{ regex });
        } finally {
            Context.exit();
        }
    }

    /**
     * <p>Matches an input against a given regex, in the <b>real</b> sense
     * of matching, that is, the regex can match anywhere in the input. Java's
     * {@link java.util.regex} makes the unfortunate mistake to make people
     * believe that matching is done on the whole input... Which is not true.
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
        final Context context = Context.enter();
        try {
            final Scriptable scope = context.newObject(sharedScope);
            scope.setPrototype(sharedScope);
            scope.setParentScope(null);
            return (Boolean) regMatch.call(context, scope, scope,
                new Object[]{ regex, input });
        } finally {
            Context.exit();
        }

    }
}
