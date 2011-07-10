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
import eel.kitchen.jsonschema.validators.ValidatorFactory;
import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class JasonSchema
{
    private final ValidatorFactory factory = new ValidatorFactory();
    private final List<String> validationErrors = new LinkedList<String>();
    private final JsonNode schema;

    public JasonSchema(final JsonNode schema)
        throws MalformedJasonSchemaException
    {
        if (schema == null)
            throw new MalformedJasonSchemaException("schema is null");
        if (!schema.isObject())
            throw new MalformedJasonSchemaException("schema is not a JSON "
                + "object");
        this.schema = schema;
    }

    public boolean validate(final JsonNode node)
    {
        if (node == null)
            throw new IllegalArgumentException("document to validate is null");
        validationErrors.clear();

        final List<String> messages = validateOneNode(schema, node, "$");

        if (messages.isEmpty())
            return true;

        validationErrors.addAll(messages);
        return false;
    }

    public List<String> getValidationErrors()
    {
        return Collections.unmodifiableList(validationErrors);
    }

    private List<String> validateOneNode(final JsonNode schema,
        final JsonNode node, final String path)
    {
        final Validator v;
        final List<String> ret = new ArrayList<String>();

        try {
            v = factory.getValidator(schema, node);
        } catch (MalformedJasonSchemaException e) {
            return Arrays.asList(String.format("%s: broken schema: %s", path,
                e.getMessage()));
        }

        if (!v.validate(node)) {
            for (final String message: v.getValidationErrors())
                ret.add(String.format("%s: %s", path, message));
            return ret;
        }

        if (!node.isContainerNode())
            return Collections.emptyList();

        if (node.isArray())
            return doValidateArray(v, node, path);

        if (node.isObject())
            return doValidateObject(v, node, path);

        return Arrays.asList(String.format("%s: could not recognize node! "
            + "BUG", path));
    }

    private List<String> doValidateArray(final Validator validator,
        final Iterable<JsonNode> node, final String path)
    {
        final List<String> messages = new LinkedList<String>();

        int i = 0;
        String subPath;
        JsonNode subSchema;

        for (final JsonNode element: node) {
            subSchema = validator.getSchemaForPath(Integer.toString(i));
            subPath = String.format("%s[%d]", path, i);
            i++;
            messages.addAll(validateOneNode(subSchema, element, subPath));
        }

        return messages;
    }

    private List<String> doValidateObject(final Validator validator,
        final JsonNode node, final String path)
    {
        final List<String> messages = new LinkedList<String>();
        final Map<String, JsonNode> fields = CollectionUtils.toMap(node.getFields());

        String subPath, fieldName;
        JsonNode subSchema, element;

        for (final Map.Entry<String, JsonNode> entry: fields.entrySet()) {
            fieldName = entry.getKey();
            element = entry.getValue();
            subSchema = validator.getSchemaForPath(fieldName);
            subPath = String.format("%s.%s", path, fieldName);
            messages.addAll(validateOneNode(subSchema, element, subPath));
        }

        return messages;
    }
}
