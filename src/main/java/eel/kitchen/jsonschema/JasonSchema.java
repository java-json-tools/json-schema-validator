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
import eel.kitchen.util.IterableJsonNode;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>The central validating class. Instantiate it with the main schema to
 * use, and call <code>validate()</code> on one instance. Reusable.</p>
 *
 * <p>Note: it uses slash-delimited paths for displays only,
 * although the draft specifies that one could use dot-delimited paths
 * instead, see sections 6.2.1 and 6.2.2</p>
 */
public final class JasonSchema
{
    /**
     * The list of validation messages
     */
    private final List<String> messages = new LinkedList<String>();

    /**
     * The associated schema
     */
    private final JsonNode schema;

    public JasonSchema(final JsonNode schema)
    {
        this.schema = schema;
    }

    /**
     * Validate an instance. Calls <code>validateOneNode</code> on this
     * instance with <code>#</code> as a path
     *
     * @param node the instance to validate
     * @return true if the instance is valid
     */
    public boolean validate(final JsonNode node)
    {
        messages.clear();

        final List<String> list = validateOneNode(schema, node, "#");

        if (list.isEmpty())
            return true;

        messages.addAll(list);
        return false;
    }

    /**
     * Get the list of validation messages
     *
     * @return the content of <code>messages</code> as an unmodifiable list
     */
    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    /**
     * Validate one instance. Calls itself recursively,
     * with the appropriate paths, if the instance to validate is a container
     * instance (objet or array).
     *
     * @param schema the schema used to validate this instance
     * @param node the instance to validate
     * @param path the slash-delimited JSON path to this instance
     * @return a list of validation messages
     *
     * @see {@link IterableJsonNode}
     * @see {@link SchemaProvider}
     */
    private static List<String> validateOneNode(final JsonNode schema,
        final JsonNode node, final String path)
    {
        final SchemaNode schemaNode = new SchemaNode(schema);
        final IterableJsonNode inode = new IterableJsonNode(node);

        final List<String> messages = new ArrayList<String>();

        if (!schemaNode.validate(node)) {
            for (final String message: schemaNode.getMessages())
                messages.add(String.format("%s: %s", path, message));
            return messages;
        }

        String fullpath, subpath;
        JsonNode subschema, subnode;
        List<String> submessages;

        final SchemaProvider provider = schemaNode.getSchemaProvider();

        for (final Map.Entry<String, JsonNode> entry: inode) {
            subpath = entry.getKey();
            subnode = entry.getValue();
            fullpath = String.format("%s/%s", path, subpath);
            subschema = provider.getSchemaForPath(subpath);
            submessages = validateOneNode(subschema, subnode, fullpath);
            messages.addAll(submessages);
        }

        return messages;
    }
}
