/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public final class RhinoHelper
{
    private static final String
        REGEX_FORMAT = "/%s/",
        REGEX_VALIDATE = "/%s/.test(\"%s\")";

    private static final ScriptEngine engine
        = new ScriptEngineManager().getEngineByName("JavaScript");

    public static boolean regexIsValid(final String regex)
    {
        try {
            engine.eval(String.format(REGEX_FORMAT, regex));
            return true;
        } catch (ScriptException e) {
            return false;
        }
    }

    public static boolean regMatch(final String regex, final String input)
    {
        final String js = String.format(REGEX_VALIDATE, regex, input);

        try {
            return (Boolean) engine.eval(js);
        } catch (ScriptException e) {
            throw new RuntimeException("Should never have reached this point!"
                + " Regex SHOULD have been validated already");
        }
    }
}
