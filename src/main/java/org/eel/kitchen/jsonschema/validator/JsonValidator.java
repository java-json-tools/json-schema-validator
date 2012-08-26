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

package org.eel.kitchen.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

/**
 * Interface implemented by all core validators
 */
public abstract class JsonValidator
{
    protected static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    protected final JsonSchemaFactory factory;
    protected final JsonNode schema;

    protected JsonValidator(final JsonSchemaFactory factory,
        final JsonNode schema)
    {
        this.factory = factory;
        this.schema = schema;
    }

    /**
     * Validate the instance, and tell whether validation should continue
     *
     * @param context the validation context
     * @param report the validation report
     * @param instance the instance to validate
     * @return true if validation should proceed
     */
    public abstract boolean validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance);

    /**
     * Return the next validator in the chain
     *
     * <p>This will be called iif
     * {@link #validate(ValidationContext, ValidationReport, JsonNode)}
     * returns {@code true}.</p>
     *
     * @return a validator
     */
    public abstract JsonValidator next();
}
