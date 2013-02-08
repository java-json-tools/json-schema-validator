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

package com.github.fge.jsonschema.old.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.metaschema.FormatAttributes;
import com.github.fge.jsonschema.metaschema.KeywordRegistry;
import com.github.fge.jsonschema.report.Domain;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;

import java.util.EnumSet;

/**
 * Base class for a format attribute
 *
 * <p>The {@code format} keyword always takes a string as an argument, and this
 * string is called a "attribute". See {@link FormatAttributes} for the list of
 * supported attributes.</p>
 *
 * <p>One important thing to remember is that a attribute will only validate a
 * given subset of JSON instance types (for instance, {@code uri} only validates
 * string instances). In the event that the instane type is not of the
 * validated types, validation <i>succeeds</i>.</p>
 *
 * <p>JSON Schema specifications allow for custom attributes to be added. This
 * implementation supports it.</p>
 *
 * <p>Final note: even though you may register format attributes to validate
 * container instances, drafts also explicitly says that you should not do so.
 * </p>
 *
 * @see KeywordRegistry
 */
public abstract class FormatAttribute
{
    /**
     * JSON instance types which this attribute can validate
     */
    private final EnumSet<NodeType> typeSet;

    /**
     * Protected constructor
     *
     * <p>Its arguments are the node types recognized by the attribute. Only
     * one attribute recognizes more than one type: {@code utc-millisec} (it
     * can validate both numbers and integers).
     * </p>
     *
     * @param first first type
     * @param other other types, if any
     */
    protected FormatAttribute(final NodeType first, final NodeType... other)
    {
        typeSet = EnumSet.of(first, other);
    }

    /**
     * Main validation function
     *
     * <p>This function only checks whether the value is of a type recognized
     * by this attribute. If so, it calls {@link #checkValue(String,
     * ValidationReport, JsonNode)}.</p>
     *
     * <p>The message template passed as an argument will have been pre-filled
     * with the keyword ({@code format}), the attribute name and the domain
     * ({@link Domain#VALIDATION}).</p>
     *
     * @param fmt the format attribute name
     * @param report the validation report
     * @param value the value to validate
     */
    public final void validate(final String fmt, final ValidationReport report,
        final JsonNode value)
    {
        if (!typeSet.contains(NodeType.getNodeType(value)))
            return;

        checkValue(fmt, report, value);
    }

    /**
     * Abstract method implemented by all attributes
     *
     * <p>It is only called if the value type is one expected by the
     * attribute, see  {@link #validate(String, ValidationReport, JsonNode)}.
     * </p>
     *
     * @param fmt the format attribute name
     * @param report the validation report
     * @param value the value to validate
     */
    public abstract void checkValue(final String fmt,
        final ValidationReport report, final JsonNode value);

    protected static Message.Builder newMsg(final String fmt)
    {
        return Domain.VALIDATION.newMessage().setKeyword("format")
            .addInfo("format", fmt);
    }
}
