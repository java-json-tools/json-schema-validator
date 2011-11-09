package org.eel.kitchen.jsonschema.draftv4;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;

public final class RequiredSyntaxValidator
    extends SyntaxValidator
{
    public RequiredSyntaxValidator(final ValidationContext context)
    {
        super(context, "required", NodeType.ARRAY);
    }

    @Override
    protected void checkFurther()
    {
        int i = -1;

        for (final JsonNode element: node) {
            i++;
            if (element.isTextual())
                continue;
            report.addMessage(String.format("array element %d is not a "
                + "property name", i));
        }
    }
}
