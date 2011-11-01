/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.syntax;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.base.NonEnumerableValidator;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.EnumSet;

public abstract class SyntaxValidator
    extends NonEnumerableValidator
{
    protected final ValidationReport report = new ValidationReport();

    protected final JsonNode schemaNode, node;
    protected final String keyword;
    protected final EnumSet<NodeType> validTypes;

    protected SyntaxValidator(final JsonNode schemaNode, final String keyword,
        final NodeType... types)
    {
        this.schemaNode = schemaNode;
        this.keyword = keyword;
        node = schemaNode.get(keyword);
        validTypes = EnumSet.copyOf(Arrays.asList(types));
    }

    protected abstract void checkFurther();

    @Override
    public final ValidationReport validate()
    {
        final NodeType nodeType = NodeType.getNodeType(node);

        if (!validTypes.contains(nodeType))
            report.addMessage("field " + keyword + " has wrong type " + nodeType
                + ", expected one of " + validTypes);
        else
            checkFurther();

        return report;
    }
}
