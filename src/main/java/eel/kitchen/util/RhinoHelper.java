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
