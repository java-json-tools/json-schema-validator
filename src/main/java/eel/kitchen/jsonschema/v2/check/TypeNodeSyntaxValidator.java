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

package eel.kitchen.jsonschema.v2.check;

import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
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

        // TODO: implement
        state.addMessage("Sorry, I only support one simple type for now");
        state.setStatus(ValidationStatus.FAILURE);
//        boolean ret = true;
//
//        for (final JsonNode element: node)
//            ret = validateOne(element) && ret;
//
//        return ret;
    }

    private void validateOne(final ValidationState state,
        final JsonNode element)
    {
        final NodeType type = NodeType.getNodeType(element);

        switch (type) {
            case OBJECT:
                // TODO: implement
                //return true;
                state.addMessage(
                    "Sorry, I only support one simple type for now");
                state.setStatus(ValidationStatus.FAILURE);
                return;
            case STRING:
                final String s = element.getTextValue();
                if (ANY.equals(s)) {
                    state.setStatus(ValidationStatus.SUCCESS);
                    break;
                }
                try {
                    NodeType.valueOf(s.toUpperCase());
                    state.setStatus(ValidationStatus.SUCCESS);
                } catch (IllegalArgumentException e) {
                    state.addMessage("unknown type " + s);
                    state.setStatus(ValidationStatus.FAILURE);
                }
                break;
            default:
                state.addMessage("invalid element of type " + type + " in "
                    + keyword + " array");
                state.setStatus(ValidationStatus.FAILURE);
        }
    }
}
