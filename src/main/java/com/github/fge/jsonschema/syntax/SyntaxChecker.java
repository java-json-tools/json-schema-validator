package com.github.fge.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.Message;

import java.util.List;

/**
 * Interface implemented by all syntax checkers
 *
 * <p>There exists one syntax checker per supported schema keyword. For the
 * recall, their role is to ensure the keyword values are well formed, so as to
 * ease the job of the associated keyword validator.</p>
 *
 * <p>You have the option to directly implement this interface, however you're
 * probably better off extending {@link AbstractSyntaxChecker} instead.</p>
 */
public interface SyntaxChecker
{
    /**
     * Check the syntax for this keyword
     *
     * @param validator the syntax validator to use
     * @param messages message list to fill in the event of a failure
     * @param schema schema to analyze       */
    void checkSyntax(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema);
}
