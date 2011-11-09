/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.syntax;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.base.AbstractValidator;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.util.NodeType;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Base implementation of a {@link SyntaxValidator}.
 */
public abstract class SyntaxValidator
    extends AbstractValidator
{
    /**
     * The report to use
     */
    protected final ValidationReport report;

    /**
     * The validation context
     */
    protected final ValidationContext context;

    /**
     * The node to check
     */
    protected final JsonNode node;

    /**
     * The list of valid types for {@link #node}
     */
    protected final EnumSet<NodeType> validTypes;

    /**
     * Constructor
     *
     * @param context the validation context
     * @param keyword the keyword to check
     * @param types the list of valid types for this keyword
     */
    protected SyntaxValidator(final ValidationContext context,
        final String keyword, final NodeType... types)
    {
        this.context = context;

        report = context.createReport(String.format(" [schema:%s]", keyword));
        node = context.getSchemaNode().get(keyword);
        validTypes = EnumSet.copyOf(Arrays.asList(types));
    }

    /**
     * Abstract method for validators which need to check more than the type
     * of the node to validate
     */
    protected abstract void checkFurther();

    /**
     * Type checks the node, then invokes {@link #checkFurther()}
     *
     * @return a validation report
     */
    @Override
    public final ValidationReport validate()
    {
        final NodeType nodeType = NodeType.getNodeType(node);

        if (!validTypes.contains(nodeType))
            report.addMessage("field has wrong type " + nodeType
                + ", expected one of " + validTypes);
        else
            checkFurther();

        return report;
    }
}
