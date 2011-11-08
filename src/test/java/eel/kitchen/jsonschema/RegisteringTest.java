package eel.kitchen.jsonschema;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.jsonschema.keyword.SimpleKeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Test for draft v4's new definition of "required"
 */

public final class RegisteringTest
{
    // Syntax validator for
    public static final class PropertiesSyntaxValidator
        extends SyntaxValidator
    {
        public PropertiesSyntaxValidator(final ValidationContext context)
        {
            super(context, "properties", NodeType.OBJECT);
        }

        @Override
        protected void checkFurther()
        {
            // Check that the elements of the object are objects,
            // therefore potential schemas
            final SortedMap<String, JsonNode> fields
                = CollectionUtils.toSortedMap(node.getFields());

            for (final Map.Entry<String, JsonNode> entry: fields.entrySet())
                if (!entry.getValue().isObject())
                    report.addMessage("value of property " + entry.getValue()
                        + " is not an object");
        }
    }

    public static final class RequiredSyntaxValidator
        extends SyntaxValidator
    {
        public RequiredSyntaxValidator(final ValidationContext context)
        {
            super(context, "required", NodeType.STRING, NodeType.ARRAY);
        }

        @Override
        protected void checkFurther()
        {
            if (node.isTextual())
                return;

            for (final JsonNode element: node)
                if (!element.isTextual())
                    report.addMessage("element is not a property name");
        }
    }

    public static final class RequiredKeywordValidator
        extends SimpleKeywordValidator
    {
        private final SortedSet<String> required = new TreeSet<String>();

        public RequiredKeywordValidator(final ValidationContext context,
            final JsonNode instance)
        {
            super(context, instance);

            final JsonNode node = schema.get("required");

            if (node.isTextual()) {
                required.add(node.getTextValue());
                return;
            }

            for (final JsonNode element: node)
                required.add(element.getTextValue());
        }

        @Override
        public void validateInstance()
        {
            final Set<String> fields
                = CollectionUtils.toSet(instance.getFieldNames());

            required.removeAll(fields);

            if (required.isEmpty())
                return;

            report.addMessage("missing dependency(ies) " + required);
        }
    }
}
