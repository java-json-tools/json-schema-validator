package org.eel.kitchen.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Interface implemented by all syntax checkers
 *
 * <p>You have the option to directly implement this interface, however there
 * exist a number of classes which you can extend.</p>
 *
 * @see SimpleSyntaxChecker
 * @see PositiveIntegerSyntaxChecker
 * @see ArrayChildrenSyntaxChecker
 */
public interface SyntaxChecker
{
    /**
     * Check the syntax for this keyword
     *
     * @param messages message list to fill in the event of a failure
     * @param schema schema to analyze
     */
    void checkSyntax(List<String> messages, JsonNode schema);
}
