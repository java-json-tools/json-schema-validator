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

package org.eel.kitchen.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 */

public final class JsonPointerV2
{
    /**
     * Regex for matching a reference token
     *
     * <p>This regex follows the {@code normal* (special normal*)*} pattern,
     * with:</p>
     * <ul>
     *     <li>{@code normal} is anything but a slash ({@code /}) or caret
     *     ({@code ^}): <b>{@code [^/^]}</b>,</li>
     *     <li>{@code special} is a caret followed by itself or a slash
     *     <i>exclusively</i>: <b>{@code ^[/^]}</b>.</li>
     * </ul>
     * <p>Note that the regex is anchored at the beginning, but not at the end.
     * </p>
     */
    private static final Pattern REFTOKEN_REGEX
        = Pattern.compile("^[^/^]*+(?:\\^[/^][^/^]*+)*+");

    /**
     * The pointer in a raw, but JSON Pointer-escaped, string.
     */
    private final String fullPointer;

    /**
     * The list of individual elements in the pointer.
     */
    private final List<String> elements = new LinkedList<String>();

    /**
     * Constructor
     *
     * <p>FIXME: unclear whether we should only accept #-prefixed inputs,
     * therefore both are accepted</p>
     *
     * @param input The input string, guaranteed not to be JSON encoded
     */
    public JsonPointerV2(final String input)
    {
        final String s = input.replaceFirst("^#", "");
        process(s);

        fullPointer = "#" + s;
    }

    /**
     * Return the reference tokens of this JSON Pointer, in order.
     *
     * @return a {@link List}
     */
    public List<String> getElements()
    {
        return Collections.unmodifiableList(elements);
    }

    /**
     * Initialize the object -- FIXME: misnamed
     *
     * <p>We read the string sequentially, a slash, then a reference token,
     * then a slash, etc. Bail out if the string is malformed.</p>
     *
     * @param input Input string, guaranteed not to be JSON encoded
     */
    private void process(final String input)
    {
        String cooked, raw;
        String victim = input;
        Matcher m;

        while (!victim.isEmpty()) {
            /*
             * Skip the /
             */
            if (!victim.startsWith("/"))
                throw new IllegalArgumentException("Illegal JSON Pointer");
            victim = victim.substring(1);

            /*
             * Grab the "cooked" reference token
             */
            m = REFTOKEN_REGEX.matcher(victim);
            cooked = m.group();
            victim = victim.substring(cooked.length());

            /*
             * Decode it, push it in the elements list
             */
            raw = refTokenDecode(cooked);
            elements.add(raw);
        }
    }

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

        for (final char c: array) {
            switch (c) {
                case '^':
                    if (!inEscape) {
                        inEscape = true;
                        continue;
                    }
                    // fall through
                default:
                    inEscape = false;
                    sb.append(c);
            }
        }

        return sb.toString();
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

        final JsonPointerV2 other = (JsonPointerV2) obj;

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
