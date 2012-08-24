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

package org.eel.kitchen.jsonschema.main;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import org.eel.kitchen.jsonschema.ref.JsonPointer;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Validation report
 *
 * <p>It is a simple {@link ListMultimap} which associates a path into the
 * validated instance (as a {@link JsonPointer} to a list of validation
 * messages.</p>
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
    private final ListMultimap<JsonPointer, String> msgMap
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
     * Add one message to the current path
     *
     * @param message the message to add
     */
    public void addMessage(final String message)
    {
        msgMap.put(path, message);
    }

    /**
     * Add a list of messages to the current path
     *
     * @param messages the list of messages
     */
    public void addMessages(final List<String> messages)
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
            for (final String msg: msgMap.get(path))
                builder.add(path + ": " + msg);

        return builder.build();
    }
}
