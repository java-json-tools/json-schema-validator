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

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.factories.SyntaxFactory;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

import java.net.URI;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Base abstract class for syntax validators.
 *
 * <p>All other syntax validators inherit from this one. It handles primary
 * type checking of keywords, and leaves it to implementations to validate
 * further if need be. The valid types for your keyword are registered at
 * construction time.</p>
 *
 * <p>If you write a validator which sole purpose is validating that a
 * keyword is of one or more primitive types, then consider using
 * {@link SimpleSyntaxValidator}, of which there are even two specialized
 * implementations:</p>
 * <ul>
 *     <li>{@link PositiveIntegerSyntaxValidator} will check that a field
 *     is an integer, and that it fits within the limits of Java's {@link
 *     Integer};</li>
 *     <li>{@link URISyntaxValidator} will check that a field is a string,
 *     and that the string is a valid {@link URI}.
 *     </li>
 * </ul>
 *
 * @see SyntaxFactory
 */
public abstract class SyntaxValidator
    implements Validator
{
    protected final String keyword;

    /**
     * The list of valid types for the currently analized keyword
     */
    private final EnumSet<NodeType> validTypes;

    /**
     * Constructor
     *
     * @param keyword the keyword to check
     * @param types the list of valid types for this keyword
     */
    protected SyntaxValidator(final String keyword, final NodeType... types)
    {
        this.keyword = keyword;
        validTypes = EnumSet.copyOf(Arrays.asList(types));
    }

    /**
     * Abstract method for validators which need to check more than the type
     * of the node to validate
     *
     * @param schema the schema to analyze
     * @param report the report to use
     * @throws JsonValidationFailureException on validation failure,
     * with the appropriate validation mode
     */
    protected abstract void checkFurther(final JsonNode schema,
        final ValidationReport report)
        throws JsonValidationFailureException;

    /**
     * Type checks the node, then invokes {@link #checkFurther(JsonNode,
     * ValidationReport)}
     *
     * @return a validation report
     */
    @Override
    public final ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final JsonNode schema = context.getSchema();

        final String prefix = String.format(" [schema:%s]", keyword);
        final ValidationReport report = context.createReport(prefix);

        final NodeType nodeType = NodeType.getNodeType(schema.get(keyword));

        if (!validTypes.contains(nodeType))
            report.message(String.format("field has wrong type %s, "
                + "expected one of %s", nodeType, validTypes));
        else
            checkFurther(schema, report);

        return report;
    }
}
