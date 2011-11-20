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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.keyword.common.DisallowKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.TypeKeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * A {@link KeywordValidator} specialized in validating the {@code type} and
 * {@code disallow} keywords.
 *
 * @see TypeKeywordValidator
 * @see DisallowKeywordValidator
 */
public abstract class AbstractTypeKeywordValidator
    extends KeywordValidator
{
    /**
     * String matching "any" type
     */
    private static final String ANY = "any";

    protected AbstractTypeKeywordValidator(final String keyword)
    {
        super(keyword);
    }

    /**
     * The core validation function
     *
     * @param context the validation context
     * @param instance the instance to validate
     * @param typeSet the primitive types
     * @param schemas the schemas found, if any
     * @return the validation report
     * @throws JsonValidationFailureException if reporting is configured to
     * throw this exception instead of collecting messages
     */
    protected abstract ValidationReport doValidate(
        final ValidationContext context, final JsonNode instance,
        final EnumSet<NodeType> typeSet, final List<JsonNode> schemas)
        throws JsonValidationFailureException;

    /**
     * The main validation function
     *
     * <p>It calls {@link #prepare(JsonNode, EnumSet, List)} to build the
     * necessary elements, then
     * {@link #doValidate(ValidationContext, JsonNode, EnumSet, List)},
     * which actually does the validation.
     *
     *
     * @param context the validation context
     * @param instance the instance to validate
     * @return the validation report
     * @throws JsonValidationFailureException if reporting is configured to
     * throw this exception instead of collecting messages
     */
    @Override
    public final ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final JsonNode typeNode = context.getSchema().get(keyword);

        final EnumSet<NodeType> typeSet = EnumSet.noneOf(NodeType.class);
        final List<JsonNode> schemas = new ArrayList<JsonNode>();

        prepare(typeNode, typeSet, schemas);
        return doValidate(context, instance, typeSet, schemas);
    }

    /**
     * Validate against a schema
     *
     * @param context the validation context
     * @param schema the found schema
     * @param instance the instance
     * @return the report
     * @throws JsonValidationFailureException if reporting is configured to
     * throw this exception instead of collecting messages
     */
    protected static ValidationReport validateSchema(
        final ValidationContext context, final JsonNode schema,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final ValidationContext ctx = context.withSchema(schema);

        return ctx.getValidator(instance).validate(ctx, instance);
    }

    /**
     * Prepare the validator by extracting the simple types and schemas from
     * the keyword node
     *
     * @param typeNode the keyword node
     * @param typeSet  the typeset to fill
     * @param schemas the list of schemas to fill
     */
    private static void prepare(final JsonNode typeNode,
        final EnumSet<NodeType> typeSet, final List<JsonNode> schemas)
    {
        if (typeNode.isTextual()) {
            addType(typeNode.getTextValue(), typeSet);
            return;
        }

        for (final JsonNode element: typeNode) {
            if (!element.isTextual()) {
                schemas.add(element);
                continue;
            }
            addType(element.getTextValue(), typeSet);
        }
    }

    /**
     * Add a primitive type to a type set
     *
     * <p>This is in a separate function because we need to take two special
     * cases into account: the first is {@link #ANY}; the second is the
     * {@code number} type: it also englobes {@code integer}, which mean we must
     * add it as well.
     * </p>
     *
     * @param s the primitive type as a string
     * @param typeSet the type set to add to
     */
    private static void addType(final String s, final EnumSet<NodeType>
        typeSet)
    {
        if (ANY.equals(s)) {
            typeSet.addAll(EnumSet.allOf(NodeType.class));
            return;
        }

        final NodeType type = NodeType.fromName(s);
        typeSet.add(type);

        if (type == NodeType.NUMBER)
            typeSet.add(NodeType.INTEGER);
    }
}
