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

package com.github.fge.jsonschema.old.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.old.syntax.PositiveIntegerSyntaxChecker;
import com.github.fge.jsonschema.old.syntax.SyntaxChecker;
import com.github.fge.jsonschema.report.Domain;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.validator.ValidationContext;

import java.util.Collections;
import java.util.EnumSet;

/**
 * Base class for a schema keyword validator
 *
 * <p>In the processing order, a keyword validator is only called after the
 * schema has been deemed syntactically valid: this is why it is practically
 * a requirement that if you create a new validating keyword, you pair it with
 * a {@link SyntaxChecker}. The constructor will be all the more simple as a
 * result.</p>
 *
 * <p>A keyword only takes effect for a certain number of JSON instance
 * types: if the instance to validate is not among these types, validation
 * succeeds.</p>
 *
 * <p>Two other abstract classes exist which you may want to extend instead of
 * this one, depending on your needs:</p>
 *
 * <ul>
 *     <li>{@link NumericKeywordValidator}, for validating numeric instances;
 *     </li>
 *     <li>{@link PositiveIntegerKeywordValidator}, for keywords accepting only
 *     a positive integer as an argument (to be paired with {@link
 *     PositiveIntegerSyntaxChecker}).</li>
 * </ul>
 */
public abstract class KeywordValidator
{
    /**
     * The keyword
     */
    protected final String keyword;

    /**
     * What types this keyword validates
     */
    private final EnumSet<NodeType> instanceTypes
        = EnumSet.noneOf(NodeType.class);

    /**
     * Constructor
     *
     * @param types the types validated by this keyword
     */
    protected KeywordValidator(final String keyword, final NodeType... types)
    {
        this.keyword = keyword;
        Collections.addAll(instanceTypes, types);
    }

    /**
     * Main validation function
     *
     * <p>Its only role is to check whether the instance type is recognized
     * by this keyword. If so, it calls {@link #validate(ValidationContext,
     * ValidationReport, JsonNode)}.</p>
     *
     * @param context the context
     * @param report the validation report
     * @param instance the instance to validate
     */
    public final void validateInstance(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        if (instanceTypes.contains(NodeType.getNodeType(instance)))
            validate(context, report, instance);
    }

    /**
     * Method which all keyword validators must implement
     *
     * @param context the context
     * @param report the validation report
     * @param instance the instance to validate
     */
    protected abstract void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance);

    /**
     * Create a new validation message template
     *
     * <p>Use this class when you want to report errors. It will have filled
     * the message with the correct domain ({@link Domain#VALIDATION})
     * and keyword.</p>
     *
     * @see Message
     *
     * @return a message builder
     */
    protected final Message.Builder newMsg()
    {
        return Domain.VALIDATION.newMessage().setKeyword(keyword);
    }

    public boolean alwaysTrue()
    {
        return false;
    }

    @Override
    public abstract String toString();
}
