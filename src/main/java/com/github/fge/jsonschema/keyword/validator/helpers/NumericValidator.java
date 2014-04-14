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

package com.github.fge.jsonschema.keyword.validator.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;

/**
 * Helper class for keywords validating numeric values
 *
 * <p>This class' role is to switch between two different validation methods:
 * {@link #validateLong(ProcessingReport, MessageBundle, FullData)} if both the
 * keyword value and instance fit exactly into a {@code long} (for performance
 * reasons), {@link #validateDecimal(ProcessingReport, MessageBundle, FullData)}
 * otherwise (for accuracy reasons).</p>
 */
public abstract class NumericValidator
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

    protected NumericValidator(final String keyword, final JsonNode digest)
    {
        super(keyword);
        number = digest.get(keyword);
        isLong = digest.get("valueIsLong").booleanValue();
    }

    @Override
    public final void validate(final Processor<FullData, FullData> processor,
        final ProcessingReport report, final MessageBundle bundle,
        final FullData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();
        if (valueIsLong(instance) && isLong)
            validateLong(report, bundle, data);
        else
            validateDecimal(report, bundle, data);
    }

    /**
     * Method to be implemented by a numeric validator if both the keyword
     * value and instance value fit into a {@code long}
     *
     * @param report the validation report
     * @param bundle the message bundle to use
     * @param data the validation data
     */
    protected abstract void validateLong(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException;

    /**
     * Method to be implemented by a numeric validator if either of the
     * keyword value or instance value do <b>not</b> fit into a {@code long}
     *
     * @param report the validation report
     * @param bundle the message bundle to use
     * @param data the validation data
     */
    protected abstract void validateDecimal(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
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
