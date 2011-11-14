/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.MissingNode;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the JSON Pointer draft, version 2
 *
 * <p><a href="http://tools.ietf.org/html/draft-pbryan-zyp-json-pointer-02">
 * JSON Pointer</a> is a draft standard defining a way to address paths
 * within JSON documents. Paths apply to container objects,
 * ie arrays or nodes. For objects, path elements are property names. For
 * arrays, they are the index in the array.</p>
 *
 * <p>The general syntax is {@code #/path/elements/here}. JSON Pointers are
 * <b>always</b> absolute: it is perfectly legal, for instance,
 * to have properties named {@code /}, {@code .} or {@code ..} in a JSON
 * object. Having relative JSON Pointers therefore make no sense at all.</p>
 *
 * <p>This means {@code /} must always be encoded (to {@code %2f}) when
 * displaying path elements. The draft also recommends that all special
 * characters defined by <a href="http://tools.ietf.org/html/rfc3986">RFC
 * 3986 (section 2.2)</a> be percent-encoded.</p>
 *
 * <p>As if things were not funny enough like that, it must also be remembered
 * that even an empty string is a valid property name. This means that JSON
 * Pointers {@code #/} and {@code #} do <b>not</b> mean the same: the first
 * means the document under path {@code ""} while the second means the root
 * of the document!
 * </p>
 *
 * <p>The draft also recommends that all special characters defined by
 * <a href="http://tools.ietf.org/html/rfc3986">RFC 3986 (section 2.2)</a> be
 * percent-encoded. This class' {@link #toString()} returns the raw
 * pointer, but also proposes a percent-encoded form using
 * {@link #toCookedString()}. It accepts both non percent-encoded pointers
 * and percent-encoded pointers as inputs, and you can also, if you wish to,
 * omit the initial {@code #}.
 * </p>
 */

public final class JsonPointer
{
    /**
     * A {@link MissingNode}, as a shortcut to return when a pointer is
     * "dangling"
     */
    private static final JsonNode MISSING
        = JsonNodeFactory.instance.objectNode().path("foo");

    /**
     * Percent-encoded representation of the {@code /} character
     */
    private static final String SLASH = "%2f";

    /**
     * Percent-encoded representation of the {@code %} character
     */
    private static final String PERCENT = "%25";

    /**
     * Regex identifying a JSON Pointer
     *
     * <p>Note that we also accept paths without an initial {@code #} for
     * convenience. Note also that it is not anchored, since we use
     * {@link Matcher#matches()} to match against inputs.</p>
     */
    private static final Pattern JSONPOINTER_REGEX
        = Pattern.compile("#?(?:/[^/]*+)*+");

    /**
     * Regex used to break the pointer into its elements
     */
    private static final Pattern PATH_SPLIT = Pattern.compile("/([^/]*+)");

    /**
     * Map pairing non percent-encoded characters to their percent-encoded
     * representation
     *
     * <p>We don't use {@link URLEncoder#encode(String, String)} since it will
     * not encode certain characters we want to see encoded (such as {@code /}),
     * and will turn the space into a {@code +}.</p>
     */
    private static final Map<String, String> encodingMap
        = new HashMap<String, String>();

    /**
     * Map pairing percent-encoded representations with they decoded
     * representations -- the reverse of {@link #encodingMap}
     */
    private static final Map<String, String> decodingMap
        = new HashMap<String, String>();

    static {
        encodingMap.put(":", "%3a");
        encodingMap.put(" ", "%20");
        encodingMap.put("?", "%3f");
        encodingMap.put("#", "%23");
        encodingMap.put("[", "%5b");
        encodingMap.put("]", "%5d");
        encodingMap.put("@", "%40");
        encodingMap.put("!", "%21");
        encodingMap.put("$", "%24");
        encodingMap.put("&", "%26");
        encodingMap.put("'", "%27");
        encodingMap.put("(", "%28");
        encodingMap.put(")", "%29");
        encodingMap.put("+", "%2b");
        encodingMap.put(",", "%2c");
        encodingMap.put("/", SLASH);
        encodingMap.put("\t", "%09");
        encodingMap.put("\r", "%0d");
        encodingMap.put("\n", "%0a");
        encodingMap.put("\b", "%08");
        encodingMap.put("\f", "%0c");

        for (final Map.Entry<String, String> entry: encodingMap.entrySet())
            decodingMap.put(entry.getValue(), entry.getKey());
    }

    /**
     * Path elements of this JSON Pointer, as decoded elements
     */
    private final List<String> elements = new LinkedList<String>();

    /**
     * Private empty constructor, used by {@link #append(String)}
     */
    private JsonPointer()
    {
    }

    /**
     * Constructor
     *
     * <p>The argument can be a complete JSON Pointer (ie, with the initial
     * {@code #}), a pointer without the initial {@code #},
     * and its argument can be either percent-encoded or not,
     * or even a mix of both.</p>
     *
     * <p>Note that it is up to the caller to ensure input is correct when
     * a percent character happens to appear in the path! This class cannot
     * guess your intents, so in that case, it is better to pass a fully
     * percent-encoded form as an argument.
     * </p>
     *
     * @param path the JSON Pointer
     * @throws IllegalArgumentException if the path is invalid
     */
    public JsonPointer(final String path)
    {
        if (path == null)
            return;

        if (!JSONPOINTER_REGEX.matcher(path).matches())
            throw new IllegalArgumentException("illegal JSON Pointer " + path);

        final Matcher matcher = PATH_SPLIT.matcher(path.replaceFirst("#", ""));

        while (matcher.find())
            elements.add(decode(matcher.group(1)));
    }

    /**
     * Return a new JsonPointer with an added <b>raw</b> path element
     *
     * <p>"Raw" means here that the path element should not be encoded in any
     * way: it is expected to be a "pure" elements,
     * where {@code /} and {@code %} are represented as such,
     * not in their encoded form.</p>
     *
     * @param pathElement the path element to append to this pointer's path
     * @return a JsonPointer with the new path
     */
    public JsonPointer append(final String pathElement)
    {
        if (pathElement == null)
            return this;

        final JsonPointer ret = new JsonPointer();
        ret.elements.addAll(elements);
        ret.elements.add(pathElement);
        return ret;
    }

    /**
     * Given a {@link JsonNode} as an argument, return the node corresponding
     * to that JsonPointer
     *
     * @param document the node to traverse
     * @return the macthing document, a {@link MissingNode} if path does not
     * exist
     */
    public JsonNode getPath(final JsonNode document)
    {
        JsonNode ret = document;

        for (final String pathElement: elements) {
            if (!ret.isContainerNode())
                return MISSING;
            if (ret.isObject())
                ret = ret.path(pathElement);
            else
                try {
                    ret = ret.path(Integer.parseInt(pathElement));
                } catch (NumberFormatException ignored) {
                    return MISSING;
                }
            if (ret.isMissingNode())
                break;
        }

        return ret;
    }

    /**
     * Turn a percent-encoded path element into its decoded form
     *
     * @param encoded the encoded path
     * @return the decoded path
     */
    private static String decode(final String encoded)
    {
        String ret = encoded;

        for (final Map.Entry<String, String> entry: decodingMap.entrySet()) {
            ret = ret.replace(entry.getKey(), entry.getValue());
            ret = ret.replace(entry.getKey().toUpperCase(), entry.getValue());
        }

        return ret.replace(PERCENT, "%");
    }

    /**
     * Turn a decoded path element into its percent-encoded form
     *
     * @param decoded the decoded path element
     * @return the percent-encoded form
     */
    private static String encode(final String decoded)
    {
        String ret = decoded.replace("%", PERCENT);

        for (final Map.Entry<String, String> entry: encodingMap.entrySet())
            ret = ret.replace(entry.getKey(), entry.getValue());

        return ret;
    }

    /**
     * Returns the percent-encoded representation of this JSON Pointer
     *
     * @return the full percent-encoded representation, including the initial
     * {@code #}
     */
    public String toCookedString()
    {
        final StringBuilder sb = new StringBuilder("#");

        for (final String element: elements)
            sb.append("/").append(encode(element));

        return sb.toString();
    }

    /**
     * Return the raw representation of this JSON Pointer
     *
     * @return the full raw path, including the initial {@code #}
     */
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("#");

        for (final String element: elements)
            sb.append("/").append(element.replace("/", SLASH));

        return sb.toString();
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;

        final JsonPointer that = (JsonPointer) o;

        return elements.equals(that.elements);
    }

    @Override
    public int hashCode()
    {
        return elements.hashCode();
    }
}
