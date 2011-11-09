/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.base.AbstractValidator;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.jsonschema.factories.KeywordFactory;

/**
 * Base abstract class for keyword validators
 *
 * <p>Keyword validators are the core of the validation process. As it is
 * guaranteed that the schema is correct when such a validator is called,
 * implementations don't have to worry about the validity of their data. They
 * just have to concentrate on validating their input.</p>
 *
 * @see KeywordFactory
 */
public abstract class KeywordValidator
    extends AbstractValidator
{
    /**
     * The validation context for this validator. For keyword validators
     * which require it, this is what will be used to spawned further
     * contexts and/or validators.
     */
    protected final ValidationContext context;

    /**
     * The validation report to use
     */
    protected final ValidationReport report;

    /**
     * The instance to validate
     */
    protected final JsonNode instance;

    protected KeywordValidator(final ValidationContext context,
        final JsonNode instance)
    {
        this.context = context;
        this.instance = instance;
        report = context.createReport();
    }
}
