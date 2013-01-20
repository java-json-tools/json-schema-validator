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

package com.github.fge.jsonschema.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Ordering;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;

import java.util.Collection;
import java.util.List;

/**
 * A validation report
 *
 * <p>A report is a map of {@link Message}s, with the path into the validated
 * instance where the error occurred as a supplementary information.</p>
 *
 * <p>You can retrieve messages either as a list of plain strings or JSON
 * (either an object or an array).</p>
 *
 * @see JsonSchema#validate(JsonNode)
 */
public final class ValidationReport
{
    /**
     * Root JSON Pointer (ie, {@code #})
     */
    private static final JsonPointer ROOT;

    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
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
    private final ListMultimap<JsonPointer, Message> msgMap
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
     * <p>The message will be added to the already existing list of messages for
     * the current path (see {@link #getPath()}, {@link #setPath(JsonPointer)}).
     * </p>
     *
     * @param message the message
     * @return true if the added message is fatal, or if {@link #fatal} is
     * already {@code true}
     */
    public boolean addMessage(final Message message)
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
    public void addMessages(final Collection<Message> messages)
    {
        for (final Message message: messages)
            if (addMessage(message))
                return;
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
     * Was there a fatal error during validation?
     *
     * <p>This implementation currently considers that all URI resolution
     * failures are fatal errors.</p>
     *
     * @return true if a fatal error has been encountered
     */
    public boolean hasFatalError()
    {
        return fatal;
    }

    /**
     * Merge with another validation report
     *
     * <p>Note that if a fatal error has been encountered, only the message
     * describing this fatal error will make it into the report. Other messages
     * are <b>discarded</b>.</p>
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
     * Get a flat list of validation messages as strings
     *
     * <p>One message has the form:</p>
     *
     * <pre>
     *     "/pointer/here": message here
     * </pre>
     *
     * <p>The list is sorted by pointer.</p>
     *
     * @return the list of messages
     */
    public List<String> getMessages()
    {
        final Iterable<JsonPointer> paths
            = Ordering.natural().sortedCopy(msgMap.keySet());

        final ImmutableList.Builder<String> builder = ImmutableList.builder();

        List<Message> messages;

        for (final JsonPointer pointer: paths) {
            messages = Ordering.natural().sortedCopy(msgMap.get(pointer));
            for (final Message msg: messages)
                builder.add("\"" + pointer + "\": " + msg);
        }

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
     * @see Message#toJsonNode()
     *
     * @return a JSON object with all validation messages
     */
    public JsonNode asJsonObject()
    {
        final ObjectNode ret = FACTORY.objectNode();

        ArrayNode node;
        List<Message> messages;

        for (final JsonPointer ptr: msgMap.keySet()) {
            node = FACTORY.arrayNode();
            messages = Ordering.natural().sortedCopy(msgMap.get(ptr));
            for (final Message message: messages)
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
     * contents of the {@link Message} and with an added member named {@code
     * path}, which contains the path into the instance where the error has
     * occurred (as a {@link JsonPointer}).</p>
     *
     * @see Message#toJsonNode()
     *
     * @return a JSON array with all validation messages
     */
    public JsonNode asJsonArray()
    {
        final ArrayNode ret = FACTORY.arrayNode();
        final Iterable<JsonPointer> paths
            = Ordering.natural().sortedCopy(msgMap.keySet());

        List<Message> messages;
        ObjectNode node;

        for (final JsonPointer ptr: paths) {
            messages = Ordering.natural().sortedCopy(msgMap.get(ptr));
            for (final Message msg: messages) {
                node = FACTORY.objectNode()
                .put("path", ptr.toString());
                // I hate to do that...
                node.putAll((ObjectNode) msg.toJsonNode());
                ret.add(node);
            }
        }

        return ret;
    }

    @Override
    public String toString()
    {
        return "current path: \"" + path + "\"; " + msgMap.size() + " messages";
    }
}
