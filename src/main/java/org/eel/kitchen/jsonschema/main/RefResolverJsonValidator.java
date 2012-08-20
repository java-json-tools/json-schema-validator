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

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RefResolverJsonValidator
    implements JsonValidator
{
    private final JsonSchemaFactory factory;
    private final Set<JsonRef> refs = new LinkedHashSet<JsonRef>();

    private SchemaNode schemaNode;
    private boolean isRef;

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
        final SchemaRegistry registry = factory.getRegistry();

        JsonNode node = schemaNode.getNode();
        SchemaContainer container = schemaNode.getContainer();

        final JsonNode refNode = node.path("$ref");
        isRef = JacksonUtils.nodeIsURI(refNode);

        if (!isRef)
            return true;

        final JsonRef origin = container.getLocator();
        JsonRef ref = JsonRef.fromURI(URI.create(refNode.textValue()));
        ref = origin.resolve(ref);

        if (!origin.contains(ref))
            try {
                container = registry.get(ref.getRootAsURI());
                context.setContainer(container);
            } catch (JsonSchemaException e) {
                report.addMessage("$ref resolution error: " + e.getMessage());
                return false;
            }

        node = ref.getFragment().resolve(container.getSchema());

        if (node.isMissingNode()) {
            report.addMessage("$ref resolution error: dangling JSON reference "
                + ref);
            return false;
        }

        if (!refs.add(ref)) {
            report.addMessage("$ref resolution error: loop detected: " + refs);
            return false;
        }

        schemaNode = new SchemaNode(container, node);
        return true;
    }

    @Override
    public JsonValidator next()
    {
        return isRef ? this : new SyntaxJsonValidator(factory, schemaNode);
    }
}
