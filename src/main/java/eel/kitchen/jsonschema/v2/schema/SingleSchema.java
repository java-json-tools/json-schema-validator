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

package eel.kitchen.jsonschema.v2.schema;

import eel.kitchen.jsonschema.v2.instance.Instance;
import eel.kitchen.jsonschema.v2.keyword.KeywordValidator;
import eel.kitchen.jsonschema.v2.keyword.KeywordValidatorProvider;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Set;

public final class SingleSchema
    implements Schema
{
    private static final KeywordValidatorProvider validatorProvider
        = KeywordValidatorProvider.getInstance();

    private final SchemaFactory factory;
    private final JsonNode schemaNode;

    private PathProvider pathProvider = ScalarPathProvider.getInstance();

    public SingleSchema(final SchemaFactory factory, final JsonNode schemaNode)
    {
        this.factory = factory;
        this.schemaNode = schemaNode;
    }

    @Override
    public Schema getSchema(final String path)
    {
        return factory.buildSingleSchema(ValidationMode.VALIDATE_NORMAL,
            pathProvider.getSchema(path));
    }

    @Override
    public void validate(final ValidationState state, final Instance instance)
    {
        final NodeType instanceType = instance.getType();

        final Set<KeywordValidator> validators
            = validatorProvider.getValidators(schemaNode, instanceType);

        final JsonNode node = instance.getRawInstance();

        ValidationState current;

        for (final KeywordValidator validator: validators) {
            current = new ValidationState(state);
            validator.validate(current, node);
            if (!current.isResolved())
                current.getNextSchema().validate(current, instance);
            state.mergeWith(current);
        }

        if (state.isFailure())
            return;

        if (!node.isContainerNode())
            return;

        pathProvider = PathProviderFactory.getPathProvider(instance);

        Schema schema;

        for (final Instance child: instance) {
            schema = getSchema(child.getPathElement());
            schema.validate(state, child);
        }
    }

    @Override
    public String toString()
    {
        return schemaNode.toString();
    }
}
