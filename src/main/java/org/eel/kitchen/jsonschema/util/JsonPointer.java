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

package org.eel.kitchen.jsonschema.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.collect.ImmutableList;
import org.eel.kitchen.jsonschema.JsonSchemaException;

import java.util.List;

/**
 * Implementation of IETF JSON Pointer draft, version 1
 *
 * <p><a href="http://tools.ietf.org/id/draft-ietf-appsawg-json-pointer-01.txt">
 * JSON Pointer</a> is a draft standard defining a way to address paths within
 * JSON documents. Paths apply to container objects, ie arrays or nodes. For
 * objects, path elements are property names. For arrays, they are the index in
 * the array.</p>
 *
 * <p>The general syntax is {@code #/path/elements/here}. A path element is
 * referred to as a "reference token" in the specification.</p>
 *
 * <p>JSON Pointers are <b>always</b> absolute: it is perfectly legal, for
 * instance, to have properties named {@code .} and {@code ..} in a JSON
 * object.</p>
 *
 * <p>Even {@code /} is a valid property name. For this reason,
 * the caret ({@code ^}) has been chosen as an escape character, but only if
 * followed by itself or {@code /}. Therefore:
 * <ul>
 *     <li>{@code ^/} becomes {@code /},</li>
 *     <li>{@code ^^} becomes {@code ^},</li>
 *     <li>{@code ^&lt;anythingelse&gt;} is illegal.</li>
 * </ul>
 *
 * <p>There are two pending traps with JSON Pointer which one must be aware of:
 * </p>
 *
 * <ul>
 *     <li><p>The initial input string <b>MAY be JSON escaped</b> (FIXME:
 *     pointer to spec)</p>; it means that, for instance,
 *     {@code "#\/"} and {@code "#/"} are the same string, since the first
 *     version is "merely" the JSON escaped version of the first.</li>
 *     <li><p>The empty string is a <b>valid</b> property name in a JSON
 *     object, which means {@code #} and {@code #/} are different. The second
 *     refers to property {@code ""} inside an object while the first refers
 *     to the JSON document itself. Implied: implementations must handle
 *     the case where the document is <i>not</i> an object (leading to a
 *     "dangling" JSON Pointer).
 *     </p></li>
 * </ul>
 *
 * <p>Fortunately, Jackson makes both points easy to handle.</p>
 *
 * <p>Final note: please note that all instances of this class are
 * <b>immutable</b>.</p>
 *
 */

public final class JsonPointer
    implements Comparable<JsonPointer>
{
    /**
     * The pointer in a raw, but JSON Pointer-escaped, string.
     */
    private final String fullPointer;

    /**
     * The list of individual elements in the pointer.
     */
    private final List<String> elements;

    /**
     * Constructor
     *
     * <p>FIXME: unclear whether we should only accept #-prefixed inputs,
     * therefore both are accepted</p>
     *
     * @param input The input string, guaranteed not to be JSON encoded
     * @throws JsonSchemaException Illegal JSON Pointer
     */
    public JsonPointer(final String input)
        throws JsonSchemaException
    {
        final String s = input == null ? "" : input.replaceFirst("^#", "");

        final ImmutableList.Builder<String> builder
            = ImmutableList.builder();
        process(s, builder);

        elements = builder.build();

        fullPointer = "#" + s;
    }

    private JsonPointer(final String fullPointer, final List<String> elements)
    {
        this.fullPointer = fullPointer;
        this.elements = elements;
    }

    /**
     * Return the reference tokens of this JSON Pointer, in order.
     *
     * @return a {@link List}
     */
    public List<String> getElements()
    {
        return elements;
    }

    /**
     * Append a path element to this pointer. Returns a new instance.
     *
     * @param element the element to append
     * @return a new instance with the element appended
     */
    public JsonPointer append(final String element)
    {
        final List<String> newElements = ImmutableList.<String>builder()
            .addAll(elements).add(element).build();

        return new JsonPointer(fullPointer + "/" + refTokenEncode(element),
            newElements);
    }

    /**
     * Append an array index to this pointer. Returns a new instance.
     *
     * <p>Note that the index validity is NOT checked for (ie,
     * you can append {@code -1} if you want to -- don't do that)</p>
     *
     * @param index the index to add
     * @return a new instance with the index appended
     */
    public JsonPointer append(final int index)
    {
        return append(Integer.toString(index));
    }

    /**
     * Resolve this JSON Pointer against a JSON instance
     *
     * @param node the node to resolve against
     * @return the result document, which may be a {@link MissingNode}
     */
    public JsonNode resolve(final JsonNode node)
    {
        JsonNode ret = node;

        for (final String pathElement : elements) {
            if (!ret.isContainerNode())
                return MissingNode.getInstance();
            if (ret.isObject())
                ret = ret.path(pathElement);
            else
                try {
                    ret = ret.path(Integer.parseInt(pathElement));
                } catch (NumberFormatException ignored) {
                    return MissingNode.getInstance();
                }
            if (ret.isMissingNode())
                break;
        }

        return ret;
    }

    /**
     * Initialize the object -- FIXME: misnamed
     *
     * <p>We read the string sequentially, a slash, then a reference token,
     * then a slash, etc. Bail out if the string is malformed.</p>
     *
     *
     * @param input Input string, guaranteed not to be JSON encoded
     * @param builder the list builder
     * @throws JsonSchemaException the input is not a valid JSON Pointer
     */
    private void process(final String input,
        final ImmutableList.Builder<String> builder)
        throws JsonSchemaException
    {
        String cooked, raw;
        String victim = input;

        while (!victim.isEmpty()) {
            /*
             * Skip the /
             */
            if (!victim.startsWith("/"))
                throw new JsonSchemaException("Illegal JSON Pointer");
            victim = victim.substring(1);

            /*
             * Grab the "cooked" reference token
             */
            cooked = getNextRefToken(victim);
            victim = victim.substring(cooked.length());

            /*
             * Decode it, push it in the elements list
             */
            raw = refTokenDecode(cooked);
            builder.add(raw);
        }
    }

    /**
     * Grab a (cooked) reference token from an input string
     *
     * <p>This method is only called from
     * {@link #process(String, ImmutableList.Builder)}, after a delimiter
     * ({@code /}) has been swallowed up. The input string is therefore
     * guaranteed to start with a reference token, which may be empty.
     * </p>
     *
     * @param input the input string
     * @return the cooked reference token
     * @throws JsonSchemaException the string is malformed
     */
    private String getNextRefToken(final String input)
        throws JsonSchemaException
    {
        final StringBuilder sb = new StringBuilder();

        final char[] array = input.toCharArray();

        boolean inEscape = false;

        for (final char c: array) {
            if (inEscape) {
                if (!(c == '^' || c == '/'))
                    throw new JsonSchemaException("illegal JSON Pointer");
                sb.append(c);
                inEscape = false;
                continue;
            }
            if (c == '/')
                break;
            if (c == '^')
                inEscape = true;
            sb.append(c);
        }

        if (inEscape)
            throw new JsonSchemaException("illegal JSON Pointer");
        return sb.toString();
    }

    /**
     * Turn a cooked reference token into a raw reference token
     *
     * <p>This means unescaping all slashes and carets. This function MUST
     * be called with a valid cooked reference token.</p>
     *
     * <p>It is called from {@link #process(String, ImmutableList.Builder)},
     * in order to push a token into {@link #elements}.</p>
     *
     * @param cooked the cooked token
     * @return the raw token
     */
    private static String refTokenDecode(final String cooked)
    {
        final StringBuilder sb = new StringBuilder(cooked.length());

        /*
         * Unescape the ^ and / if any. Elements are guaranteed to be well
         * formed (ie, ^ can only be followed by ^ or /),
         * we can therefore just skip all ^ we encounter unconditionally.
         */

        final char[] array = cooked.toCharArray();

        boolean inEscape = false;

        for (final char c : array) {
            if (c == '^' && !inEscape) {
                inEscape = true;
                continue;
            }
            inEscape = false;
            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Make a cooked reference token out of a raw element token
     *
     * <p>Used to build new pointer instances when called from {@link #append
     * (String)} or {@link #append(int)}, in order to build {@link
     * #fullPointer}.</p>
     *
     * @param raw the raw token
     * @return the cooked token
     */
    private static String refTokenEncode(final String raw)
    {
        final StringBuilder sb = new StringBuilder(raw.length());

        /*
         * Simple enough: insert a ^ in front of any ^ or /
         */
        final char[] array = raw.toCharArray();

        for (final char c: array) {
            if (c == '/' || c == '^')
                sb.append('^');
            sb.append(c);
        }

        return sb.toString();
    }

    @Override
    public int compareTo(final JsonPointer other)
    {
        return fullPointer.compareTo(other.fullPointer);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        final JsonPointer other = (JsonPointer) obj;

        return fullPointer.equals(other.fullPointer);
    }

    @Override
    public int hashCode()
    {
        return fullPointer.hashCode();
    }

    @Override
    public String toString()
    {
        return fullPointer;
    }
}
