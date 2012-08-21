/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.main.SchemaContainer;
import org.eel.kitchen.jsonschema.main.SchemaNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.util.JacksonUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * First validator in the validation chain
 *
 * <p>This validator is in charge of resolving JSON References. In most cases,
 * it will not do anything since most schemas are not JSON References.</p>
 *
 * <p>This is also the class which detects ref loops.</p>
 *
 * <p>Its {@link #next()} method always returns a {@link SyntaxJsonValidator}.
 * </p>
 */
public final class RefResolverJsonValidator
    implements JsonValidator
{
    /**
     * The schema factory
     */
    private final JsonSchemaFactory factory;

    /**
     * The schema node
     */
    private SchemaNode schemaNode;

    public RefResolverJsonValidator(final JsonSchemaFactory factory,
        final SchemaNode schemaNode)
    {
        this.factory = factory;
        this.schemaNode = schemaNode;
    }

    @Override
    public boolean validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        JsonNode node = schemaNode.getNode();

        while (JacksonUtils.nodeIsURI(node.path("$ref")))
            try {
                node = resolve(context, node);
            } catch (JsonSchemaException e) {
                report.addMessage(e.getMessage());
                return false;
            }

        schemaNode = new SchemaNode(context.getContainer(), node);
        return true;
    }

    @Override
    public JsonValidator next()
    {
        return new SyntaxJsonValidator(factory, schemaNode);
    }

    /**
     * Resolve references
     *
     * @param context the validation context
     * @param node the schema node
     * @return the resolved node
     * @throws JsonSchemaException invalid reference, loop detected, or could
     * not get content
     */
    private JsonNode resolve(final ValidationContext context,
        final JsonNode node)
        throws JsonSchemaException
    {
        SchemaContainer container = context.getContainer();

        final JsonRef source = container.getLocator();
        final JsonRef ref = JsonRef.fromString(node.get("$ref").textValue());
        final JsonRef target = source.resolve(ref);
        final Set<JsonRef> refs = new LinkedHashSet<JsonRef>();


        if (!refs.add(target))
            throw new JsonSchemaException("$ref problem: ref loop detected: "
                + refs);

        if (!source.contains(target)) {
            container = factory.getSchema(target.getRootAsURI());
            context.setContainer(container);
        }

        final JsonNode ret
            = target.getFragment().resolve(container.getSchema());

        if (ret.isMissingNode())
            throw new JsonSchemaException("$ref problem: dangling JSON ref "
                + target);

        if (!ret.isObject())
            throw new JsonSchemaException("$ref problem: JSON document is not" +
                " a schema (not an object)");

        return ret;
    }
}
