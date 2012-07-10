package org.eel.kitchen.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;

public interface SyntaxChecker
{
    void checkSyntax(ValidationContext context, JsonNode schema);
}
