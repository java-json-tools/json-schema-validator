/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.syntax;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

/**
 * Syntax validator specialized in validating "type" keywords ({@code type}
 * and {@code disallow})
 *
 * <p>It is here that the check for valid type names is performed (by
 * {@link #validateOne(ValidationReport, String, JsonNode)},
 * which means the keyword validators for both of these won't have to worry
 * about this.</p>
 */
public abstract class TypeNodeSyntaxValidator
    extends SyntaxValidator
{
    private static final String ANY = "any";

    protected TypeNodeSyntaxValidator(final String keyword)
    {
        super(keyword, NodeType.STRING, NodeType.ARRAY);
    }

    /**
     * Validate the contents of a type or disallow node
     *
     * <p>Check that:</p>
     * <ul>
     *     <li>if the keyword is a simple type, it is a known type;</li>
     *     <li>if it is an array, then its elements must be either known
     *     simple types or schemas.</li>
     * </ul>
     *
     * @see #validateOne(ValidationReport, String, JsonNode)
     */
    @Override
    protected final void checkFurther(final JsonNode schema,
        final ValidationReport report)
        throws JsonValidationFailureException
    {
        final JsonNode node = schema.get(keyword);

        if (!node.isArray()) {
            validateOne(report, node);
            return;
        }

        int i = 0;

        for (final JsonNode element : node) {
            final String prefix = String.format("array element %d: ", i++);
            validateOne(report, prefix, element);
        }
    }

    /**
     * Validate an element of a type array (also used for single element
     * validation)
     *
     * @param report the report to use
     * @param prefix the prefix to use for the report
     * @param element the element of the array to check
     * @throws JsonValidationFailureException on validation failure,
     * with the appropriate validation mode
     */
    private static void validateOne(final ValidationReport report,
        final String prefix, final JsonNode element)
        throws JsonValidationFailureException
    {
        final NodeType type = NodeType.getNodeType(element);

        switch (type) {
            /*
             * Yes, this will work even for simple element validation: the
             * fact that it must be a string or an array has been checked for
             * already!
             */
            case OBJECT:
                return;
            case STRING:
                final String s = element.getTextValue();
                if (ANY.equals(s))
                    return;
                if (NodeType.fromName(s) == null)
                    report.fail(String.format("%sunknown simple type %s",
                        prefix, s));
                return;
            default:
                report.fail(String.format("%selement has wrong type %s "
                    + "(expected a simple type or a schema)", prefix, type));
        }
    }

    /**
     * Shortcut to {@link #validateOne(ValidationReport, String,
     * JsonNode)} with an empty prefix
     *
     * @param report the report to use
     * @param element the element to check
     * @throws JsonValidationFailureException on validation failure,
     * with the appropriate validation mode
     */
    private static void validateOne(final ValidationReport report,
        final JsonNode element)
        throws JsonValidationFailureException
    {
        validateOne(report, "", element);
    }
}
