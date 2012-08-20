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
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.Set;

public class InstanceJsonValidator
    implements JsonValidator
{
    private final JsonSchemaFactory factory;
    private final SchemaNode schemaNode;

    private final Set<KeywordValidator> validators;

    private NodeType instanceType;

    public InstanceJsonValidator(final JsonSchemaFactory factory,
        final SchemaNode schemaNode)
    {
        this.factory = factory;
        this.schemaNode = schemaNode;
        validators = factory.getValidators(schemaNode.getNode());
    }

    @Override
    public boolean validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        for (final KeywordValidator validator: validators)
            validator.validateInstance(context, report, instance);

        if (!instance.isContainerNode())
            return false;

        instanceType = NodeType.getNodeType(instance);
        return true;
    }

    @Override
    public JsonValidator next()
    {
        return instanceType == NodeType.ARRAY
            ? new ArrayJsonValidator(factory, schemaNode)
            : new ObjectJsonValidator(factory, schemaNode);
    }
}
