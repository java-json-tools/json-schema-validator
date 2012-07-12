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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.bundle.KeywordBundles;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.SchemaRegistry;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.ref.JsonResolver;
import org.eel.kitchen.jsonschema.validator.JsonValidatorFactory;

public final class JsonSchemaFactory
{
    private final JsonValidatorFactory validatorFactory;
    private final JsonResolver resolver;
    private final SchemaRegistry registry;

    public JsonSchemaFactory()
    {
        validatorFactory
            = new JsonValidatorFactory(KeywordBundles.defaultBundle());
        registry = new SchemaRegistry();
        resolver = new JsonResolver(registry);
    }

    public JsonSchema create(final JsonNode node)
    {
        return create(node, "#");
    }

    public JsonSchema create(final JsonNode node, final String path)
    {
        final SchemaContainer container;
        final SchemaNode schemaNode;

        try {
            container = registry.register(node);
            schemaNode = container.lookupFragment(path);
        } catch (JsonSchemaException e) {
            return new InvalidJsonSchema(e.getMessage());
        }

        return create(container, schemaNode);
    }

    public JsonSchema create(final SchemaContainer container,
        final JsonNode node)
    {
        final SchemaNode schemaNode = new SchemaNode(container, node);

        return create(container, schemaNode);
    }

    private JsonSchema create(final SchemaContainer container,
        final SchemaNode schemaNode)
    {
        final SchemaNode realNode;

        try {
            realNode = resolver.resolve(schemaNode);
        } catch (JsonSchemaException e) {
            return new InvalidJsonSchema(e.getMessage());
        }

        return new ValidJsonSchema(this, container, realNode);
    }

    public ValidationContext newContext()
    {
        return new ValidationContext(this);
    }

    JsonValidatorFactory getValidatorFactory()
    {
        return validatorFactory;
    }
}
