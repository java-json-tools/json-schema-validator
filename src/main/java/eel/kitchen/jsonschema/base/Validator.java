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

package eel.kitchen.jsonschema.base;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.keyword.KeywordValidator;
import eel.kitchen.jsonschema.syntax.SyntaxValidator;

import java.util.Enumeration;

/**
 * The core interface for all validators, which extends {@link Enumeration}
 * of itself.
 *
 * @see {@link SyntaxValidator}
 * @see {@link KeywordValidator}
 */
public interface Validator
    extends Enumeration<Validator>
{
    /**
     * Validate the instance, either a schema ({@link SyntaxValidator} or an
     * instance ({@link KeywordValidator}).
     *
     * @return a {@link ValidationReport} of the validation
     */
    ValidationReport validate();
}
