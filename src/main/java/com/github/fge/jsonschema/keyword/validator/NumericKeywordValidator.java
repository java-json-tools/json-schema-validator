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

package com.github.fge.jsonschema.keyword.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;

public abstract class NumericKeywordValidator
    extends AbstractKeywordValidator
{
    /**
     * The keyword value
     */
    protected final JsonNode number;

    /**
     * Does the keyword value fits into a {@code long}?
     */
    private final boolean isLong;

    protected NumericKeywordValidator(final String keyword,
        final JsonNode schema)
    {
        super(keyword);
        number = schema.get(keyword);

        isLong = valueIsLong(number);
    }

    @Override
    public final void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getCurrentNode();
        if (valueIsLong(instance) && isLong)
            validateLong(report, data);
        else
            validateDecimal(report, data);
    }

    /**
     * Method to be implemented by a numeric validator if both the keyword
     * value and instance value fit into a {@code long}
     *
     * @param report the validation report
     * @param data the validation data
     */
    protected abstract void validateLong(final ProcessingReport report,
        final ValidationData data)
        throws ProcessingException;

    /**
     * Method to be implemented by a numeric validator if either of the
     * keyword value or instance value do <b>not</b> fit into a {@code long}
     *
     * @param report the validation report
     * @param data the validation data
     */
    protected abstract void validateDecimal(final ProcessingReport report,
        final ValidationData data)
        throws ProcessingException;

    @Override
    public final String toString()
    {
        return keyword + ": " + number;
    }

    /**
     * Test whether a numeric instance is a long
     *
     * <p>We use both a test on the instance type and Jackson's {@link
     * JsonNode#canConvertToLong()}. The first test is needed since the
     * latter method will also return true if the value is a decimal which
     * integral part fits into a long, and we don't want that.</p>
     *
     * @param node the node to test
     * @return true if both conditions are true
     */
    private static boolean valueIsLong(final JsonNode node)
    {
        return NodeType.getNodeType(node) == NodeType.INTEGER
            && node.canConvertToLong();
    }
}
