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

import eel.kitchen.jsonschema.SchemaNode;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>A special purpose validator to validate a schema before it is fed to
 * "real" validators. It only validates that:</p>
 * <ul>
 *     <li>the schema is not null,</li>
 *     <li>it is an object, and</li>
 *     <li>the "type" and "disallow" elements are either simple strings or
 *     array of strings.
 *     </li>
 * </ul>
 * <p>The draft specifies other possible values for type or disallow,
 * but as this implementation does not currently support this,
 * the limitation is enforced by this validator.</p>
 *
 * @see {@link SchemaNode}
 */
public final class SchemaValidator
    implements Validator
{
    /**
     * Special value for any type
     */
    private static final String ANY_TYPE = "any";

    /**
     * The schema to validate
     */
    private JsonNode schema;

    /**
     * List of error messages, if any
     */
    private final List<String> messages = new ArrayList<String>();

    /**
     * The set of supported keywords in "type" and "disallow"
     */
    private final Set<String> registeredTypes = new HashSet<String>();

    /**
     * Whether the setup is done, and whether the provided schema is valid
     */
    private boolean setupDone, validSchema;

    /**
     * Constructor.
     *
     * @param typeSet a set of types supported by this schema
     */
    public SchemaValidator(final Set<String> typeSet)
    {
        registeredTypes.addAll(typeSet);
    }

    @Override
    public Validator setSchema(final JsonNode schema)
    {
        messages.clear();
        setupDone = false;
        validSchema = false;
        this.schema = schema;
        return this;
    }

    @Override
    public boolean setup()
    {
        if (!setupDone)
            validSchema = doSetup();
        return validSchema;
    }

    /**
     * Validates the schema, see class description for the rules.
     *
     * @return true if valid
     */
    private boolean doSetup()
    {
        messages.clear();
        if (schema == null) {
            messages.add("schema is null");
            return false;
        }

        if (!schema.isObject()) {
            messages.add("schema is not an object");
            return false;
        }

        if (schema.has("$ref")) {
            messages.add("Sorry, $ref not implemented yet");
            return false;
        }

        if (schema.has("extends")) {
            messages.add("Sorry, extends not implemented yet");
            return false;
        }

        return validateTypeElement("type") && validateTypeElement("disallow");
    }

    /**
     * Only ever calls <code>setup()</code>
     *
     * @param node The instance to validate
     * @return see <code>setup()</code>
     */
    @Override
    public boolean validate(final JsonNode node)
    {
        return setup();
    }

    /**
     * Validate one of "type" or "disallow".
     *
     * @param field either "type" or "disallow"
     * @return false if the property exists and has an invalid value
     */
    private boolean validateTypeElement(final String field)
    {
        final JsonNode node = schema.get(field);

        if (node == null)
            return true;

        if (node.isTextual())
            return validateTypeName(node.getTextValue());

        if (!node.isArray()) {
            messages.add("type property is neither a string nor an array");
            return false;
        }

        for (final JsonNode element: node) {
            if (element.isObject()) {
                messages.add("Sorry, union types not implemented yet");
                return false;
            }
            if (!element.isTextual()) {
                messages.add(String.format("non string or schema element in "
                    + "%s property array", field));
                return false;
            }
            if (!validateTypeName(element.getTextValue()))
                return false;
        }

        return true;
    }

    /**
     * Validate one type name: it can be <code>ANY_TYPE</code>,
     * a value in <code>registeredTypes</code>, or any valid value in {@link
     * NodeType}.
     *
     * @param textValue the type name
     * @return true if valid
     */
    private boolean validateTypeName(final String textValue)
    {
        try {
            if (ANY_TYPE.equals(textValue))
                return true;
            if (registeredTypes.contains(textValue))
                return true;
            NodeType.valueOf(textValue.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            messages.add(String.format("unknown type %s", textValue));
            return false;
        }
    }

    @Override
    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public SchemaProvider getSchemaProvider()
    {
        return new EmptySchemaProvider();
    }
}
