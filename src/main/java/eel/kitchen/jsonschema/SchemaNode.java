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

import eel.kitchen.jsonschema.validators.SchemaProvider;
import eel.kitchen.jsonschema.validators.SchemaValidator;
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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>The main interface to validation. This is where, for instance, main schema
 * validation is done (via a {@link SchemaValidator}) and type mismatch detected
 * (an instance is to be validated by a schema, but the schema cannot validate
 * the type for the instance).</p>
 *
 * @see {@link Validator}
 * @see {@link SchemaValidator}
 * @see {@link NodeType}
 */
public final class SchemaNode
{
    /**
     * Special type keyword for any type
     */
    private static final String ANY_TYPE = "any";

    /**
     * Map of primitive JSON node types (a {@link NodeType}) as keys,
     * and a set of Validator classes as values.
     */
    private static final Map<NodeType, Class<? extends Validator>> ctors
        = new EnumMap<NodeType, Class<? extends Validator>>(NodeType.class);

    static {
        ctors.put(NodeType.ARRAY, ArrayValidator.class);
        ctors.put(NodeType.BOOLEAN, BooleanValidator.class);
        ctors.put(NodeType.INTEGER, IntegerValidator.class);
        ctors.put(NodeType.NULL, NullValidator.class);
        ctors.put(NodeType.NUMBER, NumberValidator.class);
        ctors.put(NodeType.OBJECT, ObjectValidator.class);
        ctors.put(NodeType.STRING, StringValidator.class);
    }

    /**
     * The provided schema
     */
    private final JsonNode schema;

    /**
     * The validator used for this schema
     */
    private Validator validator;

    /**
     * List of validators which will be filled in from the schema
     */
    private final Map<NodeType, Validator> validators
        = new EnumMap<NodeType, Validator>(NodeType.class);

    /**
     * Validation messages, only filled in on errors
     */
    private final List<String> messages = new LinkedList<String>();

    /**
     * Whether the schema is broken ({@link SchemaValidator} fails to
     * validate the provided schema)
     */
    private boolean brokenSchema = false;

    /**
     * Constructor. It is at this stage that the schema is validated.
     *
     * @param schema the schema
     */
    public SchemaNode(final JsonNode schema)
    {
        this.schema = schema;

        final Validator schemaValidator = new SchemaValidator();
        if (!schemaValidator.setSchema(schema).setup()) {
            messages.addAll(schemaValidator.getMessages());
            brokenSchema = true;
            return;
        }

        buildValidators();
    }

    /**
     * Fills in the <code>validators</code> map. Declares the schema as
     * broken if it cannot instantiate at least one validator (this normally
     * should not happen).
     */
    private void buildValidators()
    {
        boolean oops;

        for (final NodeType type: getSupportedTypes()) {
            oops = false;
            try {
                final Class<? extends Validator> c = ctors.get(type);
                final Validator v = c.getConstructor().newInstance();
                v.setSchema(schema);
                validators.put(type, v);
            } catch (NoSuchMethodException e) {
                oops = true;
            } catch (InvocationTargetException e) {
                oops = true;
            } catch (IllegalAccessException e) {
                oops = true;
            } catch (InstantiationException e) {
                oops = true;
            }
            if (oops) {
                messages.add("cannot instantiate validators");
                brokenSchema = true;
                return;
            }
        }
    }

    /**
     * Builds the set of possible validating types for a schema by computing
     * the values from the "type" and "disallow" fields of the schema
     *
     * @return a set of types, possibly empty
     */
    private EnumSet<NodeType> getSupportedTypes()
    {
        final EnumSet<NodeType> ret = typeSet(schema.get("type"));

        final JsonNode disallowNode = schema.get("disallow");

        if (disallowNode == null)
            return ret;

        final EnumSet<NodeType> disallow = typeSet(disallowNode);

        ret.removeAll(disallow);

        if (disallow.contains(NodeType.NUMBER))
            ret.remove(NodeType.INTEGER);

        return ret;
    }

    /**
     * Given a type node (ie, "type" or "disallow", turn it into a set of
     * {@link NodeType}s.
     *
     * @param node the node to transform
     * @return an {@link EnumSet}
     */
    private static EnumSet<NodeType> typeSet(final JsonNode node)
    {
        if (node == null)
            return EnumSet.allOf(NodeType.class);

        String s;

        if (node.isTextual()) {
            s = node.getTextValue();
            if (ANY_TYPE.equals(s))
                return EnumSet.allOf(NodeType.class);
            return EnumSet.of(NodeType.valueOf(s.toUpperCase()));
        }

        final EnumSet<NodeType> ret = EnumSet.noneOf(NodeType.class);

        for (final JsonNode element: node) {
            s = element.getTextValue();
            ret.add(NodeType.valueOf(s.toUpperCase()));
        }

        return ret;
    }

    /**
     * Check that this instance is valid, that is, the schema is valid,
     * it can validate at least one type and all validators could be
     * instantiated.
     *
     * @return true if all of the above is true
     */
    public boolean isValid()
    {
        if (brokenSchema)
            return false;

        if (validators.isEmpty()) {
            messages.add("schema does not allow any type??");
            return false;
        }

        boolean ret = true;

        for (final NodeType nodeType: validators.keySet()) {
            final Validator v = validators.get(nodeType);
            if (!v.setup()) {
                messages.addAll(v.getMessages());
                ret = false;
            }
        }

        return ret;
    }

    /**
     * Validates one instance. First checks that the instance is not null,
     * then determines its primitive type, and calls all registered
     * validators for this primitive type one after the other (if any).
     * Returns true if one succeeds.
     *
     * @param node the instance to validate
     * @return true if the above conditions are met
     */
    public boolean validate(final JsonNode node)
    {
        if (!isValid())
            return false;

        if (node == null) {
            messages.add("JSON to validate is null");
            return false;
        }

        final NodeType nodeType = NodeType.getNodeType(node);

        if (!validators.containsKey(nodeType)) {
            messages.add(String.format("node is of type %s, expected %s",
                nodeType, validators.keySet()));
            return false;
        }

        validator = validators.get(nodeType);

        if (validator.validate(node)) {
            messages.clear();
            return true;
        }

        messages.addAll(validator.getMessages());

        return false;
    }

    /**
     * Returns the schema provider associated with the successful validator.
     *
     * @return a {@link SchemaProvider}
     */
    public SchemaProvider getSchemaProvider()
    {
        return validator.getSchemaProvider();
    }

    /**
     * Returns the list of messages associated with validation
     *
     * @return the content of <code>messages</code> as an unmodifiable list
     */
    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
}
