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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.keyword.common.DisallowKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.TypeKeywordValidator;
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

    /**
     * Constructor
     *
     * @param keyword the keyword (either {@code type} or {@code disallow})
     */
    protected AbstractTypeKeywordValidator(final String keyword)
    {
        super(keyword);
    }

    /**
     * The core validation function
     *
     *
     * @param context the validation context
     * @param instance the instance to validate
     * @param typeSet the primitive types
     * @param schemas the schemas found, if any
     * @return the validation report
     * throw this exception instead of collecting messages
     */
    protected abstract ValidationReport doValidate(
        final ValidationContext context, final JsonNode instance,
        final TypeSet typeSet, final List<JsonNode> schemas);

    /**
     * The main validation function
     *
     * <p>It calls {@link #prepare(JsonNode, TypeSet, List)} to build the
     * necessary elements, then
     * {@link #doValidate(ValidationContext, JsonNode, TypeSet, List)},
     * which actually does the validation.
     *
     *
     * @param context the validation context
     * @param instance the instance to validate
     * @return the validation report
     * throw this exception instead of collecting messages
     */
    @Override
    public final ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final JsonNode typeNode = context.getSchema().get(keyword);

        final List<JsonNode> schemas = new ArrayList<JsonNode>();

        final TypeSet set = new TypeSet();

        prepare(typeNode, set, schemas);
        return doValidate(context, instance, set, schemas);
    }

    /**
     * Validate against a schema
     *
     * @param context the validation context
     * @param schema the found schema
     * @param instance the instance
     * @return the report
     */
    protected static ValidationReport validateSchema(
        final ValidationContext context, final JsonNode schema,
        final JsonNode instance)
    {
        final ValidationContext ctx = context.withSchema(schema);

        return ctx.getValidator(instance).validate(ctx, instance);
    }

    /**
     * Prepare the validator by extracting the simple types and schemas from
     * the keyword node
     *
     * @param typeNode the keyword node
     * @param set The {@link TypeSet} to fill
     * @param schemas the list of schemas to fill
     */
    private static void prepare(final JsonNode typeNode, final TypeSet set,
        final List<JsonNode> schemas)
    {
        if (typeNode.isTextual()) {
            set.addType(typeNode.textValue());
            return;
        }

        for (final JsonNode element: typeNode) {
            if (!element.isTextual()) {
                schemas.add(element);
                continue;
            }
            set.addType(element.textValue());
        }
    }

    protected static class TypeSet
    {
        private final EnumSet<NodeType> set = EnumSet.noneOf(NodeType.class);

        public void addType(final String s)
        {
            if (ANY.equals(s)) {
                set.addAll(EnumSet.allOf(NodeType.class));
                return;
            }

            final NodeType type = NodeType.fromName(s);
            set.add(type);

            if (type == NodeType.NUMBER)
                set.add(NodeType.INTEGER);
        }

        public boolean matches(final JsonNode instance)
        {
            return set.contains(NodeType.getNodeType(instance));
        }

        @Override
        public String toString()
        {
            return set.toString();
        }

        public EnumSet<NodeType> getAll()
        {
            return EnumSet.copyOf(set);
        }
    }
}
