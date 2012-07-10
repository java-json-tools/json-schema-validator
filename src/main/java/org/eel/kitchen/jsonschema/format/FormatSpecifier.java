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

package org.eel.kitchen.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.util.NodeType;

import java.util.EnumSet;

/**
 * Base class for a format specifier
 *
 * <p>The {@code format} keyword is part of draft v3, but gone in draft v4.
 * Its argument is always a string, and this string is called a "specifier".
 * The draft defines specifiers for recognizing URIs, phone numbers,
 * different date formats, and so on -- and even CSS 2.1 colors and styles!
 * </p>
 *
 * <p>This implementation covers all specifiers, the only incomplete
 * implementation being CSS styles (the {@code style} specifier).</p>
 *
 * <p>The spec allows for custom specifiers to be added. The mechanism for
 * this is not written yet, however when the day comes,
 * this is the class you will have to {@code extend}.</p>
 *
 * <p>Note that JSON instances of a type different than recognized by a
 * specifier validate successfully.</p>
 */
public abstract class FormatSpecifier
{
    /**
     * Type of values this specifier can validate
     */
    private final EnumSet<NodeType> typeSet;

    /**
     * Protected constructor
     *
     * <p>Its arguments are the node types recognized by the specifier. Only
     * one specifier recognizes more than one type: {@code utc-millisec} (it
     * can validate both numbers and integers).
     * </p>
     *
     * @param first first type
     * @param other other types, if any
     */
    protected FormatSpecifier(final NodeType first, final NodeType... other)
    {
        typeSet = EnumSet.of(first, other);
    }

    /**
     * Main validation function
     *
     * <p>This function only checks whether the value is of a type recognized
     * by this specifier. If so, it call {@link #checkValue(ValidationContext,
     * JsonNode)}.</p>
     *
     * @param context the context to use
     * @param value the value to validate
     */
    public final void validate(final ValidationContext context,
        final JsonNode value)
    {
        if (!typeSet.contains(NodeType.getNodeType(value)))
            return;

        checkValue(context, value);
    }

    /**
     * Abstract method implemented by all specifiers
     *
     * <p>It is only called if the value type is one expected by the
     * specifier, see {@link #validate(ValidationContext, JsonNode)}.</p>
     *
     * @param context the context to use
     * @param value the value to validate
     */
    abstract void checkValue(final ValidationContext context,
        final JsonNode value);
}
