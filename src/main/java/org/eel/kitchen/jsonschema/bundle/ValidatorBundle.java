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
