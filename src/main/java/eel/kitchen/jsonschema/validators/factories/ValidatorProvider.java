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

package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.Validator;
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

public final class ValidatorProvider
{
    private static final Logger logger
        = LoggerFactory.getLogger(ValidatorProvider.class);

    private static final String ANY_TYPE = "any";
    private final HashMap<String, Class<? extends ValidatorFactory>> factories
        = new HashMap<String, Class<? extends ValidatorFactory>>();

    public ValidatorProvider()
    {
        factories.put("array", ArrayValidatorFactory.class);
        factories.put("object", ObjectValidatorFactory.class);
        factories.put("string", StringValidatorFactory.class);
        factories.put("number", NumberValidatorFactory.class);
        factories.put("integer", IntegerValidatorFactory.class);
        factories.put("boolean", BooleanValidatorFactory.class);
        factories.put("null", NullValidatorFactory.class);
    }

    public Validator getValidator(final JsonNode schemaNode, final JsonNode node)
    {
        MalformedJasonSchemaException err;

        err = new MalformedJasonSchemaException("schema is null");

        if (schemaNode == null)
            return new IllegalSchemaValidator(err);

        err = new MalformedJasonSchemaException("schema is not a JSON object");

        if (!schemaNode.isObject())
            return new IllegalSchemaValidator(err);

        err = new MalformedJasonSchemaException("node to validate is null");

        if (node == null)
            return new IllegalSchemaValidator(err);

        final String type = getNodeType(node);
        err = new MalformedJasonSchemaException("cannot determine node type");

        if (type == null)
            return new IllegalSchemaValidator(err);

        final Class<? extends ValidatorFactory> c = factories.get(type);
        final Constructor<? extends ValidatorFactory> constructor;
        final ValidatorFactory factory;

        final List<String> types = validatingTypes(schemaNode);

        if (!types.contains(type))
            return new TypeMismatchValidator(types, type);

        try {
            constructor = c.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException e) {
            logger.error("cannot find appropriate constructor for factory", e);
            return null;
        }

        try {
            factory = constructor.newInstance(schemaNode);
        } catch (Exception e) {
            return new IllegalSchemaValidator(e);
        }

        final Validator validator = factory.getValidator();
        try {
            validator.setup();
        } catch (MalformedJasonSchemaException e) {
            return new IllegalSchemaValidator(e);
        }
        return validator;
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

        return null;
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

        allowed.addAll(node == null ? factories.keySet() : getTypeValues(node));

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
                return factories.keySet();
            if (!factories.containsKey(value)) {
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
            if (!factories.containsKey(value)) {
                logger.warn("no factory for type \"{}\" - did you forget " +
                    "to register it?", value);
                continue;
            }
            if (ANY_TYPE.equals(value)) {
                logger.warn("type/disallow array contains \"" + ANY_TYPE + "\"");
                return factories.keySet();
            }
            if (!ret.add(value))
                logger.warn("duplicate type entry \"{0}\"", value);
        }

        return ret;
    }

    public static final class TypeMismatchValidator
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
