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

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.ref.SchemaRegistry;
import org.eel.kitchen.jsonschema.util.JacksonUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public final class RefResolverJsonValidator
    implements JsonValidator
{
    private final JsonSchemaFactory factory;

    private final SchemaRegistry registry;
    private final Set<JsonRef> refs = new LinkedHashSet<JsonRef>();

    private SchemaNode schemaNode;

    public RefResolverJsonValidator(final JsonSchemaFactory factory,
        final SchemaNode schemaNode)
    {
        this.factory = factory;
        this.schemaNode = schemaNode;
        registry = factory.getRegistry();
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

    private JsonNode resolve(final ValidationContext context,
        final JsonNode node)
        throws JsonSchemaException
    {
        SchemaContainer container = context.getContainer();

        final JsonRef source = container.getLocator();
        final JsonRef ref = JsonRef.fromString(node.get("$ref").textValue());
        final JsonRef target = source.resolve(ref);

        if (!refs.add(target))
            throw new JsonSchemaException("$ref problem: ref loop detected: "
                + refs);

        if (!source.contains(target)) {
            container = registry.get(target.getRootAsURI());
            context.setContainer(container);
        }

        final JsonNode ret
            = target.getFragment().resolve(container.getSchema());

        if (ret.isMissingNode())
            throw new JsonSchemaException("$ref problem: dangling JSON pointer"
                + target);

        if (!ret.isObject())
            throw new JsonSchemaException("$ref problem: JSON document is not" +
                " a schema (not an object)");

        return ret;
    }
}
