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
import org.eel.kitchen.jsonschema.report.ValidationReport;

/**
 * Interface which all JSON validators must implement
 */
public interface JsonValidator
{
    /**
     * Validate the instance, and tell whether validation should continue
     *
     * <p>Note: the return value is currently unused.</p>
     *
     * @param context the validation context
     * @param report the validation report
     * @param instance the instance to validate
     * @return true if validation should proceed
     */
    boolean validate(ValidationContext context, ValidationReport report,
        JsonNode instance);
}
