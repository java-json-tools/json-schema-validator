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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import java.math.BigDecimal;

/**
 * Base class for numeric instances validators
 *
 * <p>We separate validation in two: if both the keyword value and
 * instance value are integers which fit into a {@code long},
 * we use that (for performance reasons). If one of them doesn't,
 * then we use {@link BigDecimal} instead (for accuracy reasons).
 * </p>
 *
 * <p>This means that extending this validator will require you to implement
 * two methods: {@link #validateLong(ValidationReport, JsonNode)} and
 * {@link #validateDecimal(ValidationReport, JsonNode)}.</p>
 *
 * <p>You will note that these two methods take a {@link JsonNode} as an
 * argument. Use the following methods to convert these to the appropriate
 * types:</p>
 *
 * <ul>
 *     <li>{@code long}: {@link JsonNode#longValue()};</li>
 *     <li>{@link BigDecimal}: {@link JsonNode#decimalValue()}.</li>
 * </ul>
 */
public abstract class NumericKeywordValidator
    extends KeywordValidator
{
    /**
     * The keyword value
     */
    protected final JsonNode number;

    /**
     * Does the keyword value fits into a {@code long}?
     */
    private final boolean isLong;

    /**
     * Protected constructor
     *
     * @param keyword the keyword
     * @param schema the schema
     */
    protected NumericKeywordValidator(final String keyword,
        final JsonNode schema)
    {
        super(keyword, NodeType.INTEGER, NodeType.NUMBER);
        number = schema.get(keyword);

        isLong = valueIsLong(number);
    }

    /**
     * Method to be implemented by a numeric validator if both the keyword
     * value and instance value fit into a {@code long}
     *
     * @param report the validation report
     * @param instance the instance to validate
     */
    protected abstract void validateLong(final ValidationReport report,
        final JsonNode instance);

    /**
     * Method to be implemented by a numeric validator if either of the
     * keyword value or instance value do <b>not</b> fit into a {@code long}
     *
     * @param report the validation report
     * @param instance the instance to validate
     */
    protected abstract void validateDecimal(final ValidationReport report,
        final JsonNode instance);

    /**
     * Main validation method
     *
     * <p>This is where the test for {@code long} is done on both the keyword
     * value and instance value. According to the result,
     * this method will then call either {@link #validateLong(ValidationReport,
     * JsonNode)} or {@link #validateDecimal(ValidationReport, JsonNode)}</p>
     *
     * @param context the context
     * @param report the validation report
     * @param instance the instance to validate
     */
    @Override
    public final void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        if (valueIsLong(instance) && isLong)
            validateLong(report, instance);
        else
            validateDecimal(report, instance);
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

    @Override
    public String toString()
    {
        return keyword + ": " + number;
    }
}
