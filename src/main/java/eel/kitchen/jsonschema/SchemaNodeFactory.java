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

package eel.kitchen.jsonschema;

import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.jsonschema.validators.type.ArrayValidator;
import eel.kitchen.jsonschema.validators.type.BooleanValidator;
import eel.kitchen.jsonschema.validators.type.IntegerValidator;
import eel.kitchen.jsonschema.validators.type.NullValidator;
import eel.kitchen.jsonschema.validators.type.NumberValidator;
import eel.kitchen.jsonschema.validators.type.ObjectValidator;
import eel.kitchen.jsonschema.validators.type.StringValidator;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static eel.kitchen.util.NodeType.ARRAY;
import static eel.kitchen.util.NodeType.BOOLEAN;
import static eel.kitchen.util.NodeType.INTEGER;
import static eel.kitchen.util.NodeType.NULL;
import static eel.kitchen.util.NodeType.NUMBER;
import static eel.kitchen.util.NodeType.OBJECT;
import static eel.kitchen.util.NodeType.STRING;

/**
 * <p>The {@link SchemaNode} provider. Allows to register custom types. Its
 * only user is {@link JasonSchema} for now.</p>
 *
 * @see {@link JasonSchema}
 * @see {@link SchemaNode}
 */
public final class SchemaNodeFactory
{
    /**
     * Full list of validators
     */
    private final Map<NodeType, Class<? extends Validator>> registeredValidators
        = new EnumMap<NodeType, Class<? extends Validator>>(NodeType.class);

    /**
     * Full list of registered types, may be expanded
     */
    private final Map<String, EnumSet<NodeType>> registeredTypes
        = new HashMap<String, EnumSet<NodeType>>();

    /**
     * The constructor. It starts by registering all built in validators by
     * calling <code>registerValidator()</code> below.
     */
    public SchemaNodeFactory()
    {
        registerValidator("array", ArrayValidator.class, ARRAY);
        registerValidator("boolean", BooleanValidator.class, BOOLEAN);
        registerValidator("integer", IntegerValidator.class, INTEGER);
        registerValidator("number", NumberValidator.class, NUMBER);
        registerValidator("null", NullValidator.class, NULL);
        registerValidator("object", ObjectValidator.class, OBJECT);
        registerValidator("string", StringValidator.class, STRING);
    }


    /**
     * Register one validator for a given type. Will silently fail if a
     * validator is already registered for this type.
     *
     * @param type the type name
     * @param validator the validator class
     * @param nodeTypes the list of primitive node types associated with this
     * type name
     */
    private void registerValidator(final String type,
        final Class<? extends Validator> validator, final NodeType... nodeTypes)
    {
        if (registeredTypes.containsKey(type))
            throw new RuntimeException("tried to register a second validator "
                + "for a primitive type");

        final EnumSet<NodeType> types = EnumSet.copyOf(Arrays.asList(nodeTypes));

        registeredTypes.put(type, types);

        for (final NodeType nodeType: types) {
            if (registeredValidators.containsKey(nodeType))
                throw new RuntimeException("tried to register a second "
                    + "validator for a primitive type");
            registeredValidators.put(nodeType, validator);
        }
    }

    /**
     * Spawns a schema node associated with a JSON schema
     *
     * @param schema the JSON schema
     * @return a {@link SchemaNodeFactory}
     */
    public SchemaNode getSchemaNode(final JsonNode schema)
    {
        return new SchemaNode(schema, registeredValidators, registeredTypes);
    }
}
