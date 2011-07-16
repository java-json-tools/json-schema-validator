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

import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Base {@link Validator} implementation, with the following guarantees
 * and mechanisms:</p>
 * <ul>
 *     <li>if .setSchema() is not called, then an empty schema is provided;</li>
 *     <li>provides a mechanism for self checking (register a field and its
 *     expected type);</li>
 *     <li>enforce a schema self check, if not done,
 *     before validating an instance;</li>
 *     <li>provides a mechanism to register further validators.</li>
 * </ul>
 */
public abstract class AbstractValidator
    implements Validator
{
    /**
     * An empty schema (<code>{}</code>), which will be the default is no
     * schema is provided using .setSchema()
     */
    protected static final JsonNode EMPTY_SCHEMA;

    /**
     * The schema associated with this Validator
     */
    protected JsonNode schema;

    /**
     * Lists of schema validation errors, and instance validation errors
     */
    protected final List<String>
        schemaErrors = new LinkedList<String>(),
        messages = new LinkedList<String>();

    /**
     * Map of fields in the schema instance and their expected types,
     * see .registerField()
     */
    private final Map<String, EnumSet<NodeType>> fieldMap
        = new HashMap<String, EnumSet<NodeType>>();

    /**
     * Set of further validators associated with this validators
     */
    private final Set<Validator> validators = new LinkedHashSet<Validator>();

    /**
     * booleans indicating whether the schema has been self-checked,
     * and is valid - both are false by default
     */
    private boolean setupDone = false, validSchema = false;

    static {
        try {
            EMPTY_SCHEMA = new ObjectMapper().readTree("{}");
        } catch (IOException e) {
            throw  new ExceptionInInitializerError();
        }
    }

    /**
     * Default constructor
     */
    protected AbstractValidator()
    {
        schema = EMPTY_SCHEMA;
    }

    /**
     * <p>Set the schema for this validator, clear all message lists and resets
     * self check status. If further validators are defined,
     * calls itself recursively.</p>
     *
     * @param schema The schema that this validator should use
     * @return itself
     */
    @Override
    public final Validator setSchema(final JsonNode schema)
    {
        schemaErrors.clear();
        messages.clear();
        setupDone = false;
        validSchema = false;
        this.schema = schema;
        for (final Validator v: validators)
            v.setSchema(schema);
        return this;
    }

    /**
     * <p>Checks that the schema passed in as an argument is valid,
     * and sets its internal members. Guards against multiple invocations
     * using the setupDone boolean.</p>
     *
     * @return true if the schema is valid
     */
    @Override
    public final boolean setup()
    {
        if (!setupDone) {
            validSchema = isWellFormed() && doSetup();
            for (final Validator v: validators) {
                validSchema = validSchema && v.setup();
                schemaErrors.addAll(v.getMessages());
            }
            setupDone = true;
        }

        return validSchema;
    }

    /**
     * <p>Core validation methods to be implemented by all validators in
     * order to validate the schema, EXCEPT for structure coherehcy which is
     * done by isWellFormed() below.</p>
     *
     * <p>Implementations must not forget to fill in schemaErrors.</p>
     *
     * @return true if the schema is valid.
     */
    protected abstract boolean doSetup();

    /**
     * <p>Validate an instance. Calls <code>setup()</code> before validation,
     * then <code>doValidate()</code> on self, then <code>validate()</code>
     * recursively on all declared Validators. An instance is considered
     * valid if and only if ALL Validators return true.</p>
     *
     * <p>Clears the <code>messages</code> list before validating.</p>
     *
     * @param node The instance to validate
     * @return true if the instance is valid against all declared validators
     */
    @Override
    public final boolean validate(final JsonNode node)
    {
        if (!setup())
            return false;

        messages.clear();

        boolean ret = doValidate(node);

        for (final Validator v: validators) {
            ret = ret && v.validate(node);
            messages.addAll(v.getMessages());
        }

        return ret;
    }

    /**
     * <p>Core validating routine. This is where all real checks are done.</p>
     *
     * <p>Implementations must fill in <code>messages</code> in the event of
     * errors.</p>
     *
     * @param node the instance to validate
     * @return true if the instance is valid
     */
    protected abstract boolean doValidate(final JsonNode node);

    /**
     * <p>Get the SchemaProvider associated with this schema. Only container
     * nodes (objects and arrays) will not return an {@link
     * EmptySchemaProvider} here. (they will return a {@link
     * ObjectSchemaProvider} and an {@link ArraySchemaProvider} respectively)
     * </p>
     *
     * @return the associated SchemaProvider
     */
    @Override
    public SchemaProvider getSchemaProvider()
    {
        return new EmptySchemaProvider();
    }

    /**
     * <p>Return the whole set of schema validation errors and instance
     * validation errors, in this order. Normally, only one of these is ever
     * filled in.</p>
     *
     * @return a {@link List} of all messages
     */
    @Override
    public final List<String> getMessages()
    {
        final List<String> ret = new LinkedList<String>();

        ret.addAll(schemaErrors);
        ret.addAll(messages);

        return Collections.unmodifiableList(ret);
    }

    /**
     * <p>Register a field for this validator to be used by
     * <code>isWellFormed()</code>. Note that the field name is case
     * sensitive.</p>
     *
     * @param name the name of the field to look for
     * @param type the type this field should have in order for the schema to
     * be valid
     *
     * @see NodeType
     */
    protected final void registerField(final String name, final NodeType type)
    {
        if (fieldMap.containsKey(name)) {
            fieldMap.get(name).add(type);
            return;
        }

        fieldMap.put(name, EnumSet.of(type));
    }

    /**
     * <p>Register an instantiated validator to use with this Validator.
     * Further validators will be operated in the order in which they are
     * registered.</p>
     *
     * @param validator the Validator instance to register
     */
    protected final void registerValidator(final Validator validator)
    {
        validators.add(validator);
    }

    /**
     * <p>Validate that registered fields are valid, by checking their JSON
     * type. Fills in the <code>schemaErrors</code> array if this is not the
     * case. This is ALWAYS called before <code>doSetup()</code>,
     * so this is safe to extend this class and assume fields are of the
     * given type.</p>
     *
     * <p>Note that with the current specification,
     * <i>no fields are mandatory at all</i>. This is why
     * <code>registerField()</code> does not have a parameter for this,
     * but it also means it is up to <code>doSetup()</code> to check whether
     * these fields are actually present.</p>
     *
     * @return true if the validation succeeds.
     */
    private final boolean isWellFormed()
    {
        boolean ret = true;
        EnumSet<NodeType> expected;
        NodeType actual;

        final Set<String> fieldnames = new HashSet<String>(fieldMap.keySet());
        fieldnames.retainAll(CollectionUtils.toSet(schema.getFieldNames()));

        for (final String field: fieldnames) {
            expected = fieldMap.get(field);
            actual = NodeType.getNodeType(schema.get(field));
            if (!expected.contains(actual)) {
                ret = false;
                schemaErrors.add(String.format("%s is of type %s, "
                    + "expected %s", field, actual, expected));
            }
        }

        return ret;
    }
}
