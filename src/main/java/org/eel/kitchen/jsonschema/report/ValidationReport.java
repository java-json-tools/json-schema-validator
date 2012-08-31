/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonPointer;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A validation report
 *
 * <p>Internally, it uses a {@link ListMultimap} where:</p>
 *
 * <ul>
 *     <li>keys are path into the validated instance (as {@link JsonPointer}s),
 *     </li>
 *     <li>values are (a list of) {@link ValidationMessage}s.</li>
 * </ul>
 *
 * <p>You can retrieve messages either as a list of plain strings (ordered by
 * instance path) or as JSON (ie, a {@link JsonNode}).</p>
 *
 * @see JsonSchema#validate(JsonNode)
 */
public final class ValidationReport
{
    /**
     * Root JSON Pointer (ie, {@code #})
     */
    private static final JsonPointer ROOT;

    static {
        try {
            ROOT = new JsonPointer("");
        } catch (JsonSchemaException e) {
            throw new RuntimeException("WTF??", e);
        }
    }

    /**
     * Message list
     */
    private final ListMultimap<JsonPointer, ValidationMessage> msgMap
        = ArrayListMultimap.create();

    /**
     * The current path
     */
    private JsonPointer path;

    /**
     * Create a new validation report with {@link #ROOT} as an instance path
     */
    public ValidationReport()
    {
        this(ROOT);
    }

    /**
     * Create a new validation report with an arbitraty path
     *
     * @param path the JSON Pointer
     */
    private ValidationReport(final JsonPointer path)
    {
        this.path = path;
    }

    /**
     * Get the current path of this report
     *
     * @return the path
     */
    public JsonPointer getPath()
    {
        return path;
    }

    /**
     * Set the current path of this report
     *
     * @param path the path
     */
    public void setPath(final JsonPointer path)
    {
        this.path = path;
    }

    /**
     * Add one validation message to the report
     *
     * @param message the message
     */
    public void addMessage(final ValidationMessage message)
    {
        msgMap.put(path, message);
    }

    /**
     * Add several validation messages to the report
     *
     * @param messages the collection of messages
     */
    public void addMessages(final Collection<ValidationMessage> messages)
    {
        msgMap.putAll(path, messages);
    }

    /**
     * Is this report a success?
     *
     * @return true if the message map is empty
     */
    public boolean isSuccess()
    {
        return msgMap.isEmpty();
    }

    /**
     * Merge with another validation report
     *
     * @param other the report to merge with
     */
    public void mergeWith(final ValidationReport other)
    {
        msgMap.putAll(other.msgMap);
    }

    /**
     * Make a copy of this validation report, with an empty message map and
     * the current path.
     *
     * @return the new report
     */
    public ValidationReport copy()
    {
        return new ValidationReport(path);
    }

    /**
     * Get a flat list of validation messages
     *
     * <p>One message has the form:</p>
     *
     * <pre>
     *     #/pointer/here: message here
     * </pre>
     *
     * <p>The list is sorted by pointer.</p>
     *
     * @return the list of messages
     */
    public List<String> getMessages()
    {
        final SortedSet<JsonPointer> paths
            = new TreeSet<JsonPointer>(msgMap.keySet());

        final ImmutableList.Builder<String> builder = ImmutableList.builder();

        for (final JsonPointer path: paths)
            for (final ValidationMessage msg: msgMap.get(path))
                builder.add(path + ": " + msg);

        return builder.build();
    }

    /**
     * Retrieve all messages as a {@link JsonNode}
     *
     * <p>The retrieved JSON document is an object where:</p>
     *
     * <ul>
     *     <li>keys are string representations of {@link JsonPointer}s,</li>
     *     <li>values are arrays of objects where each individual object is the
     *     JSON representation of one message.</li>
     * </ul>
     *
     * @see ValidationMessage#toJsonNode()
     *
     * @return a JSON document with all validation messages
     */
    public JsonNode asJsonNode()
    {
        final ObjectNode ret = JsonNodeFactory.instance.objectNode();
        ArrayNode node;

        for (final JsonPointer ptr: msgMap.keySet()) {
            node = JsonNodeFactory.instance.arrayNode();
            for (final ValidationMessage message: msgMap.get(ptr))
                node.add(message.toJsonNode());
            ret.put(ptr.toString(), node);
        }

        return ret;
    }

    @Override
    public String toString()
    {
        return "current path: \"" + path + "\"; " + msgMap.size() + " messages";
    }
}
