package org.eel.kitchen.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface SyntaxChecker
{
    void checkSyntax(List<String> messages, JsonNode schema);
}
