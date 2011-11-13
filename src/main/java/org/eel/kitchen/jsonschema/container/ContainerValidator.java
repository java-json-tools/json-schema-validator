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

package org.eel.kitchen.jsonschema.container;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.jsonschema.keyword.format.CacheableValidator;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * <p>>A specialized {@link Validator} implementation for validating container
 * nodes (ie, array or object JSON instances).</p>
 *
 * <p>The particularity of these validators is that not only do they need to
 * validate the structure of the instance itself, they must also,
 * if the structure is valid, spawn validators for all the subnodes of the
 * object instance.
 * </p>
 *
 * @see ArrayValidator
 * @see ObjectValidator
 */
public abstract class ContainerValidator
    implements CacheableValidator
{
    /**
     * An empty schema, always true
     */
    protected static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    /**
     * The {@link Validator} which validates the structure of the instance
     * itself
     */
    private final CacheableValidator validator;

    /**
     * Constructor
     *
     * @param validator the structure validator, see {@link #validator}
     */
    protected ContainerValidator(final CacheableValidator validator)
    {
        this.validator = validator;
    }

    protected abstract void buildPathProvider(final JsonNode schema);

    protected abstract Collection<JsonNode> getSchemas(final String path);

    /**
     * Validate all children nodes, in the event that structure validation
     * succeeds
     */
    protected abstract ValidationReport validateChildren(
        final ValidationContext context, final JsonNode instance);

    @Override
    public final ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();
        report.mergeWith(validator.validate(context, instance));

        if (!report.isSuccess())
            return report;

        buildPathProvider(context.getSchemaNode());
        report.mergeWith(validateChildren(context, instance));

        return report;
    }

    @Override
    public Iterator<CacheableValidator> iterator()
    {
        return Collections.<CacheableValidator>emptyList().iterator();
    }
}
