package org.eel.kitchen.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;

public interface SyntaxChecker
{
    void checkSyntax(ValidationReport report, JsonNode schema);
}
