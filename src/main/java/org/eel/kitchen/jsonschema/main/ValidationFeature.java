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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.main;

/**
 * List of validation features
 *
 * @see ValidationConfig#enable(ValidationFeature)
 * @see ValidationConfig#disable(ValidationFeature)
 */
public enum ValidationFeature
{
    /**
     * Should the validation fail at the first error encountered (see {@link
     * JsonValidationFailureException})
     */
    FAIL_FAST,
    /**
     * Should schema syntax checking be skipped (use at your own risks!)
     */
    SKIP_SCHEMACHECK
}
