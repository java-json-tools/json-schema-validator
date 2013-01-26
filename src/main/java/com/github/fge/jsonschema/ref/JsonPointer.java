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

package com.github.fge.jsonschema.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.report.Domain;
import com.github.fge.jsonschema.report.Message;
import com.google.common.base.CharMatcher;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of IETF JSON Pointer draft, version 8
 *
 * <p><a href="http://tools.ietf.org/html/draft-ietf-appsawg-json-pointer-08">
 * JSON Pointer</a> is an IETF draft defining a way to address paths within JSON
 * documents. Paths apply to containers, ie arrays or nodes. For objects, path
 * elements are member names. For arrays, they are indices in the array
 * (starting from 0).</p>
 *
 * <p>The general syntax is {@code #/path/elements/here}. A path element is
 * referred to as a "reference token" in the specification.</p>
 *
 * <p>The difficulty solved by JSON Pointer is that any JSON String is valid as
 * an object member name. These are all valid object member names, and all of
 * them can be addressed by using JSON Pointer:</p>
 *
 * <ul>
 *     <li>{@code ""} -- the empty string;</li>
 *     <li>{@code "/"};</li>
 *     <li>{@code "0"};</li>
 *     <li>{@code "-1"};</li>
 *     <li>{@code "."}, {@code ".."}, {@code "../.."}.</li>
 * </ul>
 *
 * <p>The latter example is the reason why a JSON Pointer is <b>always</b>
 * absolute.</p>
 *
 * <p>All instances of this class are immutable (and therefore thread safe).</p>
 */

public final class JsonPointer
    extends JsonFragment
{
    private static final JsonPointer EMPTY
        = new JsonPointer("", ImmutableList.<String>of());

    /*
     * Reference token separator
     */
    private static final CharMatcher SLASH = CharMatcher.is('/');

    /*
     * Escape character in a "cooked" element
     */
    private static final CharMatcher ESCAPE_CHAR = CharMatcher.is('~');

    /*
     * "0": for array index reference token needs
     */
    private static final CharMatcher ZERO = CharMatcher.is('0');

    /*
     * Replacement map for getting a raw reference token out of a cooked one
     * ("~0" becomes "~" and "~1" becomes "/".
     *
     * This is a BiMap so that it can also be used in the reverse situation.
     */
    private static final BiMap<Character, Character> ESCAPE_REPLACEMENT_MAP
        = new ImmutableBiMap.Builder<Character, Character>()
            .put('0', '~')
            .put('1', '/')
            .build();

    private static final CharMatcher ESCAPED;
    private static final CharMatcher SPECIAL;

    static {
        CharMatcher escaped = CharMatcher.NONE, special = CharMatcher.NONE;

        for (final Character c1: ESCAPE_REPLACEMENT_MAP.keySet())
            escaped = escaped.or(CharMatcher.is(c1));

        for (final Character c2: ESCAPE_REPLACEMENT_MAP.values())
            special = special.or(CharMatcher.is(c2));

        ESCAPED = escaped.precomputed();
        SPECIAL = special.precomputed();
    }

    /**
     * The list of individual elements in the pointer.
     */
    private final List<String> elements;

    /**
     * Return an empty pointer
     *
     * @return a statically created empty JSON Pointer
     */
    public static JsonPointer empty()
    {
        return EMPTY;
    }

    /**
     * Constructor
     *
     * @param input The input string, guaranteed not to be JSON encoded
     * @throws JsonSchemaException Illegal JSON Pointer
     */
    public JsonPointer(final String input)
        throws JsonSchemaException
    {
        super(input);
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        decode(input, builder);

        elements = builder.build();
    }

    private JsonPointer(final String fullPointer, final List<String> elements)
    {
        super(fullPointer);
        this.elements = elements;
    }

    /**
     * Append a pointer to the current pointer
     *
     * @param other the other pointer
     * @return a new instance with the pointer appended
     */
    public JsonPointer append(final JsonPointer other)
    {
        final List<String> newElements = ImmutableList.<String>builder()
            .addAll(elements).addAll(other.elements).build();
        final String newPath = asString + other.asString;
        return new JsonPointer(newPath, newElements);
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

        return new JsonPointer(asString + '/' + refTokenEncode(element),
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

    @Override
    public JsonNode resolve(final JsonNode node)
    {
        JsonNode ret = node;

        for (final String pathElement : elements) {
            if (!ret.isContainerNode())
                return MissingNode.getInstance();
            ret = ret.isObject()
                ? ret.path(pathElement)
                : ret.path(arrayIndexFor(pathElement));
            if (ret.isMissingNode())
                break;
        }

        return ret;
    }

    @Override
    public boolean isEmpty()
    {
        return asString.isEmpty();
    }

    @Override
    public boolean isPointer()
    {
        return true;
    }

    /**
     * Return this pointer as a series of JSON Pointers starting from the
     * beginning
     *
     * @return an unmodifiable list
     */
    public List<JsonPointer> asElements()
    {
        final ImmutableList.Builder<JsonPointer> builder
            = ImmutableList.builder();

        JsonPointer ptr;
        String fullptr;

        for (final String raw: elements) {
            fullptr = '/' + refTokenEncode(raw);
            ptr = new JsonPointer(fullptr, ImmutableList.of(raw));
            builder.add(ptr);
        }

        return builder.build();
    }

    public boolean isParentOf(final JsonPointer other)
    {
        return Collections.indexOfSubList(other.elements, elements) == 0;
    }

    /**
     * Initialize the object
     *
     * <p>We read the string sequentially, a slash, then a reference token,
     * then a slash, etc. Bail out if the string is malformed.</p>
     *
     * @param input Input string, guaranteed not to be JSON/URI encoded
     * @param builder the list builder
     * @throws JsonSchemaException the input is not a valid JSON Pointer
     */
    private static void decode(final String input,
        final ImmutableList.Builder<String> builder)
        throws JsonSchemaException
    {
        String cooked, raw;
        String victim = input;

        while (!victim.isEmpty()) {
            /*
             * Skip the /
             */
            if (!victim.startsWith("/")) {
                final Message.Builder msg
                    = newMsg("reference token not preceeded by '/'");
                throw new JsonSchemaException(msg.build());
            }
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
     * {@link #decode(String, ImmutableList.Builder)}, after a delimiter
     * ({@code /}) has been swallowed up. The input string is therefore
     * guaranteed to start with a reference token, which may be empty.
     * </p>
     *
     * @param input the input string
     * @return the cooked reference token
     * @throws JsonSchemaException the string is malformed
     */
    private static String getNextRefToken(final String input)
        throws JsonSchemaException
    {
        final StringBuilder sb = new StringBuilder();

        final char[] array = input.toCharArray();

        /*
         * If we encounter a /, this is the end of the current token.
         *
         * If we encounter a ~, ensure that what follows is either 0 or 1.
         *
         * If we encounter any other character, append it.
         */

        boolean inEscape = false;

        for (final char c: array) {
            if (inEscape) {
                if (!ESCAPED.matches(c)) {
                    final Message.Builder msg = newMsg("bad escape sequence: " +
                        "'~' not followed by a valid token")
                        .addInfo("allowed", ESCAPE_REPLACEMENT_MAP.keySet())
                        .addInfo("found", Character.valueOf(c));
                    throw new JsonSchemaException(msg.build());
                }
                sb.append(c);
                inEscape = false;
                continue;
            }
            if (SLASH.matches(c))
                break;
            if (ESCAPE_CHAR.matches(c))
                inEscape = true;
            sb.append(c);
        }

        if (inEscape)
            throw new JsonSchemaException(newMsg("bad escape sequence: '~' " +
                "not followed by any token").build());
        return sb.toString();
    }

    /**
     * Turn a cooked reference token into a raw reference token
     *
     * <p>This means we replace all occurrences of {@code ~0} with {@code ~},
     * and all occurrences of {@code ~1} with {@code /}.</p>
     *
     * <p>It is called from {@link #decode}, in order to push a token into
     * {@link #elements}.</p>
     *
     * @param cooked the cooked token
     * @return the raw token
     */
    private static String refTokenDecode(final String cooked)
    {
        final StringBuilder sb = new StringBuilder(cooked.length());

        /*
         * Replace all occurrences of "~0" with "~", and all occurrences of
         * "~1" with "/".
         *
         * The input is guaranteed to be well formed.
         */

        final char[] array = cooked.toCharArray();

        boolean inEscape = false;

        for (final char c: array) {
            if (ESCAPE_CHAR.matches(c)) {
                inEscape = true;
                continue;
            }
            if (inEscape) {
                sb.append(ESCAPE_REPLACEMENT_MAP.get(c));
                inEscape = false;
            } else
                sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Make a cooked reference token out of a raw element token
     *
     * @param raw the raw token
     * @return the cooked token
     */
    private static String refTokenEncode(final String raw)
    {
        final StringBuilder sb = new StringBuilder(raw.length());

        /*
         * Replace all occurrences of "~" with "~0" and all occurrences of "/"
         * with "~1".
         */
        final char[] array = raw.toCharArray();

        for (final char c: array)
            if (SPECIAL.matches(c))
                sb.append('~').append(ESCAPE_REPLACEMENT_MAP.inverse().get(c));
            else
                sb.append(c);

        return sb.toString();
    }

    private static Message.Builder newMsg(final String reason)
    {
        return Domain.REF_RESOLVING.newMessage().setKeyword("$ref")
            .setMessage("illegal JSON Pointer").addInfo("reason", reason);
    }

    /**
     * Return an array index corresponding to the given path element
     *
     * @param pathElement the path element as a string
     * @return the index, or -1 if the index is invalid
     */
    private static int arrayIndexFor(final String pathElement)
    {
        /*
         * Empty? No dice.
         */
        if (pathElement.isEmpty())
            return -1;
        /*
         * Leading zeroes are not allowed in number-only elements for arrays.
         * But then, 0 followed by anything else than a number is invalid as
         * well. So, if the string starts with '0', return 0 if the token length
         * is 1 or -1 otherwise.
         */
        if (ZERO.matches(pathElement.charAt(0)))
            return pathElement.length() == 1 ? 0 : -1;

        /*
         * Otherwise, parse as an int. If we can't, -1.
         */
        try {
            return Integer.parseInt(pathElement);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }
}
