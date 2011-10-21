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
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>The main interface to validation. A new instance is spawned by a {@link
 * SchemaNodeFactory} when validating a schema. This is where, for instance,
 * main schema validation is done (via a {@link SchemaValidator}) and type
 * mismatch detected (an instance is to be validated by a schema,
 * but the schema cannot validate the type for the instance).</p>
 *
 * <p>It is spawned with a schema and the list of all possible types and
 * validators, and removes from its internal structures the types which the
 * provided schema (if valid!) cannot validate.</p>
 *
 * @see {@link Validator}
 * @see {@link SchemaValidator}
 * @see {@link SchemaNodeFactory}
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
    private final Map<NodeType, Class<? extends Validator>> ctors
        = new EnumMap<NodeType, Class<? extends Validator>>(NodeType.class);

    /**
     * Map of type names as keys, and the corresponding node types as values
     */
    private final Map<String, EnumSet<NodeType>> types
        = new HashMap<String, EnumSet<NodeType>>();

    /**
     * The provided schema
     */
    private final JsonNode schema;

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
     * The validator which could successfully validate an instance
     */
    private Validator successful;

    /**
     * Constructor. It is at this stage that the schema is validated.
     *
     * @param schema the schema
     * @param allValidators the list of all registered validators
     * @param allTypes the list of all registered types
     */
    public SchemaNode(final JsonNode schema,
        final Map<NodeType, Class<? extends Validator>> allValidators,
        final Map<String, EnumSet<NodeType>> allTypes)
    {
        this.schema = schema;
        final Validator schemaValidator = new SchemaValidator(allTypes.keySet());
        if (!schemaValidator.setSchema(schema).setup()) {
            messages.addAll(schemaValidator.getMessages());
            brokenSchema = true;
            return;
        }

        ctors.putAll(allValidators);
        types.putAll(allTypes);

        setup();
    }

    /**
     * Cleans up the <code>types</code> and <code>ctors</code> map. It can
     * declare the schema as  broken if it cannot validate types (think:
     * { "disallow": "any" } for instance). After cleanup is done, calls
     * <code>buildValidators()</code>.
     */
    private void setup()
    {
        final Set<String>
            typeNames = getValidatingTypes(),
            allTypes = new HashSet<String>(types.keySet());

        allTypes.removeAll(typeNames);
        EnumSet<NodeType> goodbye;

        for (final String unsupported: allTypes) {
            goodbye = types.remove(unsupported);
            for (final NodeType nodeType: goodbye)
                ctors.remove(nodeType);
        }

        if (ctors.isEmpty()) {
            messages.add("schema does not allow any type??");
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

        for (final NodeType type: ctors.keySet()) {
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
    private Set<String> getValidatingTypes()
    {
        final Set<String> ret = new HashSet<String>();

        final JsonNode
            typeNode = schema.get("type"),
            disallowNode = schema.get("disallow");

        if (typeNode == null)
            ret.addAll(types.keySet());
        else if (typeNode.isTextual())
            ret.add(typeNode.getTextValue());
        else
            for (final JsonNode element: typeNode)
                ret.add(element.getTextValue());

        if (ret.remove(ANY_TYPE))
            ret.addAll(types.keySet());

        if (disallowNode == null)
            return ret;

        final Set<String> disallow = new HashSet<String>();

        if (disallowNode.isTextual())
            disallow.add(disallowNode.getTextValue());
        else
            for (final JsonNode element: disallowNode)
                disallow.add(element.getTextValue());

        if (disallow.remove(ANY_TYPE))
            return Collections.emptySet();

        ret.removeAll(disallow);
        if (ret.contains("integer") && disallow.contains("number"))
            ret.remove("integer");

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

        final Validator v = validators.get(nodeType);

        if (v.validate(node)) {
            successful = v;
            messages.clear();
            return true;
        }
        messages.addAll(v.getMessages());

        return false;
    }

    /**
     * Returns the schema provider associated with the successful validator.
     *
     * @return a {@link SchemaProvider}
     */
    public SchemaProvider getSchemaProvider()
    {
        return successful.getSchemaProvider();
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
