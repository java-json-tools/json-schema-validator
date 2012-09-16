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

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.format.FormatBundle;
import org.eel.kitchen.jsonschema.ref.SchemaNode;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.validator.JsonValidator;
import org.eel.kitchen.jsonschema.validator.JsonValidatorCache;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import java.util.EnumSet;

/**
 * The main validation class
 *
 * <p>This class is thread-safe, immutable and concurrent-friendly: you can
 * validate as many inputs as you want with one instance.</p>
 *
 * @see JsonSchemaFactory
 */
public final class JsonSchema
{
    /**
     * The validator cache
     */
    private final JsonValidatorCache cache;

    /**
     * The feature set
     */
    private final EnumSet<ValidationFeature> features;

    /**
     * The format bundle
     */
    private final FormatBundle formatBundle;

    /**
     * The schema node
     */
    private final SchemaNode schemaNode;

    /**
     * Constructor, package private
     *
     * @param cache the validator cache
     * @param features the feature set
     * @param schemaNode the schema node
     */
    JsonSchema(final JsonValidatorCache cache,
        final EnumSet<ValidationFeature> features, final FormatBundle bundle,
        final SchemaNode schemaNode)
    {
        this.cache = cache;
        this.features = EnumSet.copyOf(features);
        this.schemaNode = schemaNode;
        formatBundle = bundle;
    }

    /**
     * The main validation function
     *
     * @param instance the JSON document to validate
     * @return a {@link ValidationReport}
     */
    public ValidationReport validate(final JsonNode instance)
    {
        final ValidationContext context
            = new ValidationContext(cache, features, formatBundle);

        final ValidationReport report = new ValidationReport();

        final JsonValidator validator = cache.getValidator(schemaNode);

        validator.validate(context, report, instance);

        return report;
    }
}
