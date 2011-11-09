package org.eel.kitchen.jsonschema.draftv4;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.JsonValidator;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.keyword.AlwaysTrueKeywordValidator;
import org.eel.kitchen.util.JsonLoader;
import org.eel.kitchen.util.NodeType;

import java.io.IOException;

/**
 * Example of registering new validators.
 *
 * <p>For this example we take the new definition of "required": in draft
 * v3, it is an attribute of a schema attached to a property in
 * "properties", but in draft v4, it becomes a first level keyword which is
 * an array of property names required for the instance to validate.
 * </p>
 *
 * <p>We therefore have written three new validators:</p>
 * <ul>
 *     <li>{@link PropertiesSyntaxValidator}, which will override the one
 *     currently present (since we now don't need, and in fact must not,
 *     check for "required" attributes in schemas;</li>
 *     <li>{@link RequiredSyntaxValidator}, which will check that the
 *     "required" keyword, when present, is an array of strings (ie,
 *     property names);</li>
 *     <li>{@link RequiredKeywordValidator}, which will validate an instance
 *     with this new definition of the "required" keyword.
 *     </li>
 * </ul>
 */
public final class DraftV4Example
{
    public static void main(final String... args)
        throws IOException
    {
        final JsonNode testNode = JsonLoader.fromResource("/draftv4/example"
            + ".json");

        final JsonNode schema = testNode.get("schema");

        final JsonValidator validator = new JsonValidator(schema);

        validator.unregisterValidator("properties");
        validator.unregisterValidator("required");
        validator.registerValidator("properties",
            PropertiesSyntaxValidator.class, AlwaysTrueKeywordValidator.class,
            NodeType.OBJECT);
        validator.registerValidator("required",
            RequiredSyntaxValidator.class, RequiredKeywordValidator.class,
            NodeType.OBJECT);

        ValidationReport report;

        report = validator.validate(testNode.get("good"));

        System.out.println("Valid instance: " + report.isSuccess());

        report = validator.validate(testNode.get("bad"));

        System.out.println("Valid instance: " + report.isSuccess());

        for (final String msg: report.getMessages())
            System.out.println(msg);
    }
}
