/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.base.CharMatcher;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

import static com.github.fge.jsonschema.messages.JsonRefMessages.*;

/**
 * Implementation of IETF JSON Pointer draft, version 9
 *
 * <p><a href="http://tools.ietf.org/html/draft-ietf-appsawg-json-pointer-09">
 * JSON Pointer</a> is an IETF draft defining a way to address paths within JSON
 * values (including non container values).</p>
 *
 * <p>An individual entry of a JSON Pointer is called a reference token. For
 * JSON Objects, a reference token is a member name. For arrays, it is an index.
 * Indices start at 0. Note that array indices written with a leading 0 are
 * considered to be failing (ie, {@code 0} is OK but {@code 00} is not).</p>
 *
 * <p>The general syntax is {@code /reference/tokens/here}. A JSON Pointer
 * <i>may</i> be empty, in which case this refers to the JSON value itself.</p>
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
    /**
     * The empty pointer
     */
    private static final JsonPointer EMPTY
        = new JsonPointer("", ImmutableList.<String>of());

    /**
     * Reference token separator
     */
    private static final CharMatcher SLASH = CharMatcher.is('/');

    /**
     * Escape character in a "cooked" element
     */
    private static final CharMatcher ESCAPE_CHAR = CharMatcher.is('~');

    /**
     * "0": for array index reference token needs
     */
    private static final CharMatcher ZERO = CharMatcher.is('0');

    /**
     * Replacement map for getting a raw reference token out of a cooked one
     *
     * <p>{@code ~0} becomes {@code ~} and {@code ~1} becomes {@code /}.</p>
     *
     * <p>This is a {@link BiMap} so that it can also be used in the reverse
     * situation.</p>
     *
     * @see #refTokenEncode(String)
     * @see #refTokenDecode(String)
     */
    private static final BiMap<Character, Character> ESCAPE_REPLACEMENT_MAP
        = new ImmutableBiMap.Builder<Character, Character>()
            .put('0', '~')
            .put('1', '/')
            .build();

    /**
     * Character matcher for an escaped reference token
     *
     * <p>This is built from {@link #ESCAPE_REPLACEMENT_MAP}'s keys.</p>
     *
     * @see #refTokenDecode(String)
     */
    private static final CharMatcher ESCAPED;

    /**
     * Character matcher for a raw reference token
     *
     * <p>This is built from {@link #ESCAPE_REPLACEMENT_MAP}'s values.</p>
     *
     * @see #refTokenEncode(String)
     */
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
     * The list of individual reference tokens, in order.
     */
    private final List<String> refTokens;

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
     * @throws JsonReferenceException illegal JSON Pointer
     */
    public JsonPointer(final String input)
        throws JsonReferenceException
    {
        super(input);
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        decode(input, builder);

        refTokens = builder.build();
    }

    /**
     * Private constructor for building a pointer with all pointer elements
     * (reference tokens, full string representation) already computed
     *
     * @param fullPointer the pointer as a string
     * @param refTokens the reference tokens
     */
    private JsonPointer(final String fullPointer, final List<String> refTokens)
    {
        super(fullPointer);
        this.refTokens = refTokens;
    }

    /**
     * Static private constructor to build a pointer out of a list of reference
     * tokens
     *
     * @param refTokens the list of reference tokens
     * @return a newly constructed JSON Pointer
     */
    private static JsonPointer fromElements(final List<String> refTokens)
    {
        if (refTokens.isEmpty())
            return empty();

        final StringBuilder sb = new StringBuilder();

        for (final String raw: refTokens)
            sb.append('/').append(refTokenEncode(raw));

        return new JsonPointer(sb.toString(), refTokens);
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
            .addAll(refTokens).addAll(other.refTokens).build();
        if (newElements.isEmpty())
            return empty();
        final String newPath = asString + other.asString;
        return new JsonPointer(newPath, newElements);
    }

    /**
     * Append a reference token as a string to this pointer.
     *
     * @param element the element to append
     * @return a new instance with the element appended
     */
    public JsonPointer append(final String element)
    {
        final List<String> newElements = ImmutableList.<String>builder()
            .addAll(refTokens).add(element).build();

        return new JsonPointer(asString + '/' + refTokenEncode(element),
            newElements);
    }

    /**
     * Append an array index to this pointer.
     *
     * <p>Note that the index validity is NOT checked for (ie, you can append
     * {@code -1} if you want to -- don't do that)</p>
     *
     * @param index the index to add
     * @return a new instance with the index appended
     */
    public JsonPointer append(final int index)
    {
        return append(Integer.toString(index));
    }

    /**
     * Apply the pointer to a JSON value and return the result
     *
     * <p>If the pointer fails to look up a value, a {@link MissingNode} is
     * returned.</p>
     *
     * @param node the node to apply the pointer to
     * @return the resulting node
     */
    @Override
    public JsonNode resolve(final JsonNode node)
    {
        JsonNode ret = node;

        for (final String pathElement : refTokens) {
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
     * @return an unmodifiable list of JSON Pointers
     */
    public List<JsonPointer> asElements()
    {
        final ImmutableList.Builder<JsonPointer> builder
            = ImmutableList.builder();

        for (final String raw: refTokens)
            builder.add(fromElements(ImmutableList.of(raw)));

        return builder.build();
    }

    /**
     * Return true if this JSON Pointer is "parent" of another one
     *
     * <p>That is, its number of reference tokens is less than, or equal to,
     * the other pointer's, and its first reference tokens are the same.</p>
     *
     * <p>This means that this will also return true if the pointers are equal.
     * </p>
     *
     * @param other the other pointer
     * @return true if this pointer is the parent of the other
     */
    public boolean isParentOf(final JsonPointer other)
    {
        return Collections.indexOfSubList(other.refTokens, refTokens) == 0;
    }

    /**
     * Relativize a pointer to the current pointer
     *
     * <p>If {@link #isParentOf(JsonPointer)} returns false, this will return
     * the other pointer.</p>
     *
     * <p>Otherwise, it will return a pointer containing all reference tokens
     * following this pointer's reference tokens. For instance, relativizing
     * {@code /a/b} against {@code /a/b/c} gives {@code /c}.</p>
     *
     * <p>If the pointers are the same, it will return an empty pointer.</p>
     *
     * @param other the pointer to relativize this pointer to
     * @return a relativized pointer
     */
    public JsonPointer relativize(final JsonPointer other)
    {
        if (!isParentOf(other))
            return other;
        final List<String> list = other.refTokens.subList(refTokens.size(),
            other.refTokens.size());
        return fromElements(list);
    }

    /**
     * Initialize the object
     *
     * <p>We read the string sequentially, a slash, then a reference token,
     * then a slash, etc. Bail out if the string is malformed.</p>
     *
     * @param input Input string, guaranteed not to be JSON/URI encoded
     * @param builder the list builder
     * @throws JsonReferenceException the input is not a valid JSON Pointer
     */
    private static void decode(final String input,
        final ImmutableList.Builder<String> builder)
        throws JsonReferenceException
    {
        String cooked, raw;
        String victim = input;

        while (!victim.isEmpty()) {
            /*
             * Skip the /
             */
            if (!victim.startsWith("/")) {
                final ProcessingMessage message = new ProcessingMessage()
                    .message("illegal JSON Pointer")
                    .put("reason", "reference token not preceeded by '/'");
                throw new JsonReferenceException(message);
            }
            victim = victim.substring(1);

            /*
             * Grab the "cooked" reference token
             */
            cooked = getNextRefToken(victim);
            victim = victim.substring(cooked.length());

            /*
             * Decode it, push it in the refTokens list
             */
            raw = refTokenDecode(cooked);
            builder.add(raw);
        }
    }

    /**
     * Grab a (cooked) reference token from an input string
     *
     * <p>This method is only called from {@link #decode(String,
     * ImmutableList.Builder)}, after a delimiter ({@code /}) has been swallowed
     * up. The input string is therefore guaranteed to start with a reference
     * token, which may be empty.</p>
     *
     * @param input the input string
     * @return the cooked reference token
     * @throws JsonReferenceException the string is malformed
     */
    private static String getNextRefToken(final String input)
        throws JsonReferenceException
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
                    final ProcessingMessage message
                        = new ProcessingMessage().message(ILLEGAL_POINTER)
                            .put("reason", ILLEGAL_ESCAPE)
                            .put("allowed", ESCAPE_REPLACEMENT_MAP.keySet())
                            .put("found", Character.valueOf(c));
                    throw new JsonReferenceException(message);
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

        if (inEscape) {
            final ProcessingMessage message
                = new ProcessingMessage().message(ILLEGAL_POINTER)
                .put("reason", EMPTY_ESCAPE);
            throw new JsonReferenceException(message);
        }
        return sb.toString();
    }

    /**
     * Turn a cooked reference token into a raw reference token
     *
     * <p>This means we replace all occurrences of {@code ~0} with {@code ~},
     * and all occurrences of {@code ~1} with {@code /}.</p>
     *
     * <p>It is called from {@link #decode}, in order to push a reference token
     * into {@link #refTokens}.</p>
     *
     * @param cooked the cooked token
     * @return the raw token
     */
    private static String refTokenDecode(final String cooked)
    {
        final StringBuilder sb = new StringBuilder(cooked.length());

        /*
         * Replace all occurrences of "~0" with "~", and all occurrences of "~1"
         * with "/".
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
     * Make a cooked reference token out of a raw reference token
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

    /**
     * Return an array index corresponding to the given reference token
     *
     * <p>If no array index can be found, -1 is returned. As the result is used
     * with {@link JsonNode#path(int)}, we are guaranteed correct results, since
     * this will return a {@link MissingNode} in this case.</p>
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
         * Leading zeroes are not allowed in number-only refTokens for arrays.
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
