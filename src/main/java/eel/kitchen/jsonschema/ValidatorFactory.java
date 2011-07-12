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

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.jsonschema.validators.errors.IllegalSchemaValidator;
import eel.kitchen.jsonschema.validators.errors.TypeMismatchValidator;
import eel.kitchen.jsonschema.validators.providers.ArrayValidatorProvider;
import eel.kitchen.jsonschema.validators.providers.BooleanValidatorProvider;
import eel.kitchen.jsonschema.validators.providers.IntegerValidatorProvider;
import eel.kitchen.jsonschema.validators.providers.NullValidatorProvider;
import eel.kitchen.jsonschema.validators.providers.NumberValidatorProvider;
import eel.kitchen.jsonschema.validators.providers.ObjectValidatorProvider;
import eel.kitchen.jsonschema.validators.providers.StringValidatorProvider;
import eel.kitchen.jsonschema.validators.providers.ValidatorProvider;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public final class ValidatorFactory
{
    private static final Logger logger
        = LoggerFactory.getLogger(ValidatorFactory.class);

    private static final String ANY_TYPE = "any";
    private final HashMap<String, Class<? extends ValidatorProvider>> providers
        = new HashMap<String, Class<? extends ValidatorProvider>>();

    public ValidatorFactory()
    {
        providers.put("array", ArrayValidatorProvider.class);
        providers.put("object", ObjectValidatorProvider.class);
        providers.put("string", StringValidatorProvider.class);
        providers.put("number", NumberValidatorProvider.class);
        providers.put("integer", IntegerValidatorProvider.class);
        providers.put("boolean", BooleanValidatorProvider.class);
        providers.put("null", NullValidatorProvider.class);
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

        final String type = JasonHelper.getNodeType(node);
        err = new MalformedJasonSchemaException("cannot determine node type");

        if (type == null)
            return new IllegalSchemaValidator(err);

        final Class<? extends ValidatorProvider> c = providers.get(type);
        final Constructor<? extends ValidatorProvider> constructor;
        final ValidatorProvider provider;

        final List<String> types = validatingTypes(schemaNode);

        if (!types.contains(type))
            return new TypeMismatchValidator(types, type);

        try {
            constructor = c.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException e) {
            logger.error("cannot find appropriate constructor for provider", e);
            return null;
        }

        try {
            provider = constructor.newInstance(schemaNode);
        } catch (Exception e) {
            return new IllegalSchemaValidator(e);
        }

        final Validator validator = provider.getValidator();
        try {
            validator.setup();
        } catch (MalformedJasonSchemaException e) {
            return new IllegalSchemaValidator(e);
        }
        return validator;
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

        allowed.addAll(node == null ? providers.keySet() : getTypeValues(node));

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
                return providers.keySet();
            if (!providers.containsKey(value)) {
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

        final Collection<String> ret = new LinkedList<String>();

        for (final JsonNode element: node) {
            if (!element.isTextual()) {
                logger.error("type/disallow array contains non string " +
                    "element, ignored");
                continue;
            }
            value = element.getTextValue();
            if (!providers.containsKey(value)) {
                logger.warn("no factory for type \"{}\" - did you forget " +
                    "to register it?", value);
                continue;
            }
            if (ANY_TYPE.equals(value)) {
                logger.warn("type/disallow array contains \"" + ANY_TYPE + '\"');
                return providers.keySet();
            }
            if (!ret.add(value))
                logger.warn("duplicate type entry \"{0}\"", value);
        }

        return ret;
    }
}
