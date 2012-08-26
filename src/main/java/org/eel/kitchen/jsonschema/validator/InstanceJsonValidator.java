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
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.Set;

/**
 * Third validator in the validation chain
 *
 * <p>This is the first validator which actually checks the validated instance.
 * Validation stops if the instance is not a container instance (ie, an array or
 * an object).</p>
 *
 * <p>Its {@link #next()} method will return either of an
 * {@link ArrayJsonValidator} or an {@link ObjectJsonValidator} depending on the
 * instance type.</p>
 */
public final class InstanceJsonValidator
    extends JsonValidator
{
    private final Set<KeywordValidator> validators;

    private NodeType instanceType;

    InstanceJsonValidator(final JsonSchemaFactory factory,
        final JsonNode schema)
    {
        super(factory, schema);
        validators = validationContext.getValidators(schema);
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
            ? new ArrayJsonValidator(factory, schema)
            : new ObjectJsonValidator(factory, schema);
    }
}
