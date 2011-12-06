package org.eel.kitchen.jsonschema.bundle;

import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;

import java.util.Map;
import java.util.Set;

public interface ValidatorBundle
{
    Map<String, SyntaxValidator> syntaxValidators();

    Set<String> ignoredSyntaxValidators();

    Map<NodeType, Map<String, KeywordValidator>> keywordValidators();

    Map<NodeType, Set<String>> ignoredKeywordValidators();

    void registerValidator(final String keyword, final SyntaxValidator sv,
        final KeywordValidator kv, final NodeType... types);

    void unregisterValidator(final String keyword);
}
