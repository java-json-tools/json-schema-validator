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

package eel.kitchen.jsonschema.v2.syntax;

import eel.kitchen.jsonschema.v2.keyword.ValidationStatus;
import eel.kitchen.jsonschema.v2.schema.ValidationState;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

public abstract class TypeNodeSyntaxValidator
    extends MultipleTypeSyntaxValidator
{
    private static final String ANY  = "any";

    private final String keyword;

    protected TypeNodeSyntaxValidator(final String keyword)
    {
        super(keyword, NodeType.STRING, NodeType.ARRAY);
        this.keyword = keyword;
    }

    @Override
    public final void validate(final ValidationState state,
        final JsonNode schema)
    {
        super.validate(state, schema);

        if (state.isFailure())
            return;

        final JsonNode node = schema.get(keyword);

        if (!node.isArray()) {
            validateOne(state, node);
            return;
        }

        for (final JsonNode element: node)
            validateOne(state, element);
    }

    private void validateOne(final ValidationState state,
        final JsonNode element)
    {
        final NodeType type = NodeType.getNodeType(element);

        switch (type) {
            case OBJECT:
                return;
            case STRING:
                final String s = element.getTextValue();
                try {
                    if (!ANY.equals(s))
                        NodeType.valueOf(s.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    state.addMessage(fieldName + ": unknown type " + s);
                    state.setStatus(ValidationStatus.FAILURE);
                }
                return;
            default:
                state.addMessage("invalid element of type " + type + " in "
                    + keyword + " array");
                state.setStatus(ValidationStatus.FAILURE);
        }
    }
}
