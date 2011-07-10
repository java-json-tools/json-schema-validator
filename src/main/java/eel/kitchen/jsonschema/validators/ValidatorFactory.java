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

package eel.kitchen.jsonschema.validators;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class ValidatorFactory
{
    private static final Logger logger
        = LoggerFactory.getLogger(ValidatorFactory.class);

    private static final String ANY_TYPE = "any";
    private final HashMap<String, Class<? extends Validator>> validators
        = new HashMap<String, Class<? extends Validator>>();

    public ValidatorFactory()
    {
        validators.put("enum", EnumValidator.class);
        validators.put("array", ArrayValidator.class);
        validators.put("object", ObjectValidator.class);
        validators.put("string", StringValidator.class);
        validators.put("number", NumberValidator.class);
        validators.put("integer", IntegerValidator.class);
        validators.put("boolean", BooleanValidator.class);
        validators.put("null", NullValidator.class);
    }

    public Validator getValidator(final JsonNode schemaNode,
        final JsonNode node)
        throws MalformedJasonSchemaException
    {
        final String type = schemaNode.has("enum") ? "enum" : getNodeType(node);
        final Class<? extends Validator> c = validators.get(type);
        final Constructor<? extends Validator> constructor;
        final Validator ret;

        final List<String> types = validatingTypes(schemaNode);

        if (!types.contains(type))
            return new TypeMismatchValidator(types, type);

        try {
            constructor = c.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException e) {
            logger.error("Could not instantiate constructor of an already " +
                "registered validator!!", e);
            return null;
        }

        try {
            ret = constructor.newInstance(schemaNode);
        } catch (Exception e) {
            throw new MalformedJasonSchemaException("cannot instantiate " +
                "constructor", e);
        }

        ret.setup();
        return ret;
    }

    public void registerValidator(final String typeName,
        final Class<? extends Validator> validator)
        throws MalformedJasonSchemaException
    {
        if (validators.containsKey(typeName))
            throw new MalformedJasonSchemaException("there is already a " +
                "validator for type " + typeName);

        try {
            validator.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException e) {
            throw new MalformedJasonSchemaException("cannot find " +
                "constructor", e);
        }
        validators.put(typeName, validator);
    }

    private static String getNodeType(final JsonNode node)
    {
        if (node.isArray())
            return "array";
        if (node.isObject())
            return "object";
        if (node.isTextual())
            return "string";
        if (node.isNumber())
            return node.isIntegralNumber() ? "integer" : "number";
        if (node.isBoolean())
            return "boolean";
        if (node.isNull())
            return "null";

        throw new RuntimeException("BUG: cannot determine node type");
    }

    private List<String> validatingTypes(final JsonNode schema)
    {
        JsonNode node;
        final List<String>
            disallowed = new ArrayList<String>(),
            allowed = new ArrayList<String>();

        node = schema.get("disallow");

        if (node != null)
            disallowed.addAll(getTypeValues(node));

        node = schema.get("type");

        allowed.addAll(node == null ? validators.keySet() : getTypeValues(node));

        allowed.removeAll(disallowed);

        if (allowed.contains("integer") && disallowed.contains("number")) {
            logger.warn("schema allows integer but disallows number, "
                + "removing integer");
            allowed.remove("integer");
        }
        return Collections.unmodifiableList(allowed);
    }

    private Collection<String> getTypeValues(final JsonNode node)
    {
        String value;

        if (node.isTextual()) {
            value = node.getTextValue();
            if (ANY_TYPE.equals(value))
                return validators.keySet();
            if (!validators.containsKey(value)) {
                logger.warn(
                    "unknown type \"{}\", " + "did you forget to register it?",
                    value);
                return Collections.emptySet();
            }
            return Arrays.asList(value);
        }

        if (!node.isArray()) {
            logger.error("type/disallow field is not a string or an array, " +
                "assuming empty");
            return Collections.emptySet();
        }

        final Collection<String> ret = new HashSet<String>();

        for (final JsonNode element: node) {
            if (!element.isTextual()) {
                logger.error("type/disallow array contains non string " +
                    "element, ignored");
                continue;
            }
            value = element.getTextValue();
            if (!validators.containsKey(value)) {
                logger.warn("no validator for type \"{}\" - did you forget " +
                    "to register it?", value);
                continue;
            }
            if (ANY_TYPE.equals(value)) {
                logger.warn("type/disallow array contains \"" + ANY_TYPE + "\"");
                return validators.keySet();
            }
            if (!ret.add(value))
                logger.warn("duplicate type entry \"{0}\"", value);
        }

        return ret;
    }

    public final class TypeMismatchValidator
        implements Validator
    {
        private final String message;

        public TypeMismatchValidator(final List<String> types, final String actual)
        {
            switch (types.size()) {
                case 0:
                    message = "schema does not allow any type??";
                    break;
                case 1:
                    message = String.format("node is of type %s, expected %s",
                        actual, types.get(0));
                    break;
                default:
                    message = String.format("node is of type %s, "
                        + "expected one of %s", actual, types);
            }
        }

        @Override
        public void setup()
        {
        }

        @Override
        public boolean validate(final JsonNode node)
        {
            return false;
        }

        @Override
        public List<String> getValidationErrors()
        {
            return Arrays.asList(message);
        }

        @Override
        public JsonNode getSchemaForPath(final String subPath)
        {
            return null;
        }
    }
}
