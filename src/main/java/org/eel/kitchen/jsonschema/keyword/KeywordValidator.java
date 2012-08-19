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
import org.eel.kitchen.jsonschema.ValidationContext;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.Collections;
import java.util.EnumSet;

/**
 * Base class for a schema keyword validator
 *
 * <p>Keyword validators will only ever be called if the keyword syntax is
 * correct. This makes one less problem to handle.</p>
 *
 * <p>A keyword only takes effect for a certain number of JSON instance
 * types: if the instance to validate is not among these types,
 * validation succeeds.</p>
 */
public abstract class KeywordValidator
{
    /**
     * What types this keyword validates
     */
    protected final EnumSet<NodeType> instanceTypes
        = EnumSet.noneOf(NodeType.class);

    /**
     * Constructor
     *
     * @param types the types validated by this keyword
     */
    protected KeywordValidator(final NodeType... types)
    {
        Collections.addAll(instanceTypes, types);
    }

    /**
     * Main validation function
     *
     * <p>Its only role is to check whether the instance type is recognized
     * by this keyword. If so, it calls {@link #validate(ValidationContext,
     * JsonNode)}.</p>
     *
     * <p>In the opposite scenario, it means this keyword cannot validate
     * this particular instance: it is therefore considered valid.</p>
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
}
