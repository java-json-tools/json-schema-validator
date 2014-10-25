/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.processors.data.FullData;
import com.google.common.collect.Queues;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Deque;

/**
 * Class to keep track of instance pointer/schema pairs during validation
 *
 * <p>This class helps to detect scenarios where a same schema is visited
 * more than once for a same instance pointer. For instance, any instance
 * validated against this schema:</p>
 *
 * <pre>
 *     { "oneOf": [ {}, { "$ref": "#" } ] }
 * </pre>
 *
 * <p>will trigger a validation loop.</p>
 *
 * <p>Simply keeping track of instance pointer/schema pairs alone is not
 * enough; it is sometimes perfectly legal to revisit a same pair during
 * validation (for instance, alternative definitions of a container referring to
 * one common schema for the same child). For this reason we use a stack, to
 * which we {@link #push(FullData) push} pointer/schema pairs which are then
 * {@link #pop() pop}ped when validation is complete.</p>
 */
@ParametersAreNonnullByDefault
final class ValidationStack
{
    /*
     * Sentinel which is always the first element of the stack; we use it in
     * order to simplify the pop code.
     */
    private static final Element NULL_ELEMENT = new Element(null, null);

    /*
     * Queue of visited contexts
     */
    private final Deque<Element> validationQueue = Queues.newArrayDeque();

    /*
     * Head error message when a validation loop is detected
     */
    private final String errmsg;

    /*
     * Current instance pointer and associated schema stack.
     */
    private JsonPointer pointer = null ;
    private Deque<SchemaURI> schemaURIs = null;

    ValidationStack(final String errmsg)
    {
        this.errmsg = errmsg;
    }

    /**
     * Push one validation context onto the stack
     *
     * <p>A {@link FullData} instance contains all the necessary information to
     * decide what is to be done here. The most important piece of information
     * is the pointer into the instance being analyzed:</p>
     *
     * <ul>
     *     <li>if it is the same pointer, then we attempt to append the schema
     *     URI into the validation element; if there is a duplicate, this is a
     *     validation loop, throw an exception;</li>
     *     <li>otherwise, a new element is created with the new instance pointer
     *     and the schema URI.</li>
     * </ul>
     *
     * @param data the validation data
     * @throws ProcessingException instance pointer is unchanged, and an
     * attempt is made to validate it using the exact same schema
     *
     * @see #pop()
     */
    void push(final FullData data)
        throws ProcessingException
    {
        final JsonPointer ptr = data.getInstance().getPointer();
        final SchemaURI schemaURI = new SchemaURI(data.getSchema());

        if (ptr.equals(pointer)) {
            if (schemaURIs.contains(schemaURI))
                throw new ProcessingException(validationLoopMessage(data));
            schemaURIs.addLast(schemaURI);
            return;
        }

        validationQueue.addLast(new Element(pointer, schemaURIs));
        pointer = ptr;
        schemaURIs = Queues.newArrayDeque();
        schemaURIs.addLast(schemaURI);
    }

    /**
     * Exit the current validation context
     *
     * <p>Here we remove the last schema URI visited; from then on, we have two
     * scenarios:</p>
     *
     * <ul>
     *     <li>if the list of schema URIs is not empty, we do not take any
     *     further action;</li>
     *     <li>if the list is empty, validation of this part of the instance is
     *     complete; we therefore remove the tail of our {@link #validationQueue
     *     validation queue} and change the current validation context.</li>
     * </ul>
     *
     * <p>Note that it is safe to pop the outermost validation context, since
     * the first item in the validation queue is guaranteed to be {@link
     * #NULL_ELEMENT}.</p>
     */
    void pop()
    {
        schemaURIs.removeLast();
        if (!schemaURIs.isEmpty())
            return;

        final Element element = validationQueue.removeLast();
        pointer = element.pointer;
        schemaURIs = element.schemaURIs;
    }

    private static final class Element
    {
        private final JsonPointer pointer;
        private final Deque<SchemaURI> schemaURIs;

        private Element(@Nullable final JsonPointer pointer,
            @Nullable final Deque<SchemaURI> schemaURIs)
        {
            this.pointer = pointer;
            this.schemaURIs = schemaURIs;
        }
    }

    private static final class SchemaURI
    {
        private final JsonRef locator;
        private final JsonPointer pointer;

        private SchemaURI(final SchemaTree tree)
        {
            locator = tree.getContext();
            pointer = tree.getPointer();
        }

        @Override
        public int hashCode()
        {
            return locator.hashCode() ^ pointer.hashCode();
        }

        @Override
        public boolean equals(@Nullable final Object obj)
        {
            if (obj == null)
                return false;
            if (this == obj)
                return true;
            if (getClass() != obj.getClass())
                return false;
            final SchemaURI other = (SchemaURI) obj;
            return locator.equals(other.locator)
                && pointer.equals(other.pointer);
        }

        @Override
        public String toString()
        {
            final URI tmp;
            try {
                tmp = new URI(null, null, pointer.toString());
            } catch (URISyntaxException e) {
                throw new RuntimeException("How did I get there??", e);
            }
            return locator.toURI().resolve(tmp).toString();
        }
    }

    private ProcessingMessage validationLoopMessage(final FullData input)
    {
        final ArrayNode node = JacksonUtils.nodeFactory().arrayNode();
        for (final SchemaURI uri: schemaURIs)
            node.add(uri.toString());
        return input.newMessage()
            .put("domain", "validation")
            .setMessage(errmsg)
            .putArgument("alreadyVisited", new SchemaURI(input.getSchema()))
            .putArgument("instancePointer", pointer.toString())
            .put("validationPath", node);
    }
}
