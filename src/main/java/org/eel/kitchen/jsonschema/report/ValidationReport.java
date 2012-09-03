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
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonPointer;

import java.util.Collection;
import java.util.Comparator;
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

    private boolean fatal = false;

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
     * @return true if the added message is fatal, or if {@link #fatal} is
     * already {@code true}
     */
    public boolean addMessage(final ValidationMessage message)
    {
        if (fatal)
            return true;

        if (message.isFatal()) {
            fatal = true;
            msgMap.clear();
        }
        msgMap.put(path, message);
        return fatal;
    }

    /**
     * Add several validation messages to the report
     *
     * @param messages the collection of messages
     */
    public void addMessages(final Collection<ValidationMessage> messages)
    {
        for (final ValidationMessage message: messages)
            if (addMessage(message))
                return;
    }

    public int size()
    {
        return msgMap.size();
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

    public boolean hasFatalError()
    {
        return fatal;
    }

    /**
     * Merge with another validation report
     *
     * @param other the report to merge with
     */
    public void mergeWith(final ValidationReport other)
    {
        if (fatal)
            return;

        if (other.fatal) {
            msgMap.clear();
            fatal = true;
        }

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
     * Retrieve all messages as a JSON object
     *
     * <p>The retrieved JSON document is an object where:</p>
     *
     * <ul>
     *     <li>keys are string representations of {@link JsonPointer}s,</li>
     *     <li>values are arrays of objects where each individual object is the
     *     JSON representation of one message.</li>
     * </ul>
     *
     * <p>Note: the returned {@link JsonNode} is mutable.</p>
     *
     * @see ValidationMessage#toJsonNode()
     *
     * @return a JSON object with all validation messages
     */
    public JsonNode asJsonObject()
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

    /**
     * Return the list of validation messages as a JSON array
     *
     * <p>This method makes its best to order validation messages correctly.</p>
     *
     * <p>Each message in the resulting array is a JSON object, with the
     * contents of the {@link ValidationMessage} and with an added member named
     * {@code path}, which contains the path into the instance where the error
     * has occurred (as a {@link JsonPointer}).</p>
     *
     * @see ValidationMessage#toJsonNode()
     * @see MessageComparator
     *
     * @return a JSON array with all validation messages
     */
    public JsonNode asJsonArray()
    {
        final ArrayNode ret = JsonNodeFactory.instance.arrayNode();
        ObjectNode node;

        final SortedSet<JsonPointer> set
            = new TreeSet<JsonPointer>(msgMap.keySet());

        final SortedSet<ValidationMessage> messages
            = Sets.newTreeSet(MessageComparator.instance);

        for (final JsonPointer ptr: set) {
            messages.addAll(msgMap.get(ptr));
            for (final ValidationMessage msg: messages) {
                node = JsonNodeFactory.instance.objectNode()
                .put("path", ptr.toString());
                // I hate to do that...
                node.putAll((ObjectNode) msg.toJsonNode());
                ret.add(node);
            }
            messages.clear();
        }

        return ret;
    }

    @Override
    public String toString()
    {
        return "current path: \"" + path + "\"; " + msgMap.size() + " messages";
    }

    private static class MessageComparator
        implements Comparator<ValidationMessage>
    {
        private static final Comparator<ValidationMessage> instance
            = new MessageComparator();

        @Override
        public int compare(final ValidationMessage msg1,
            final ValidationMessage msg2)
        {
            int ret;

            ret = msg1.getDomain().compareTo(msg2.getDomain());
            if (ret != 0)
                return ret;
            ret = msg1.getKeyword().compareTo(msg2.getKeyword());
            if (ret != 0)
                return ret;
            return msg1.getMessage().compareTo(msg2.getMessage());
        }
    }
}
