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

package org.eel.kitchen.jsonschema.keyword;

import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.factories.KeywordFactory;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;

/**
 * Base abstract class for keyword validators
 *
 * <p>Keyword validators are the core of the validation process. As it is
 * guaranteed that the schema is correct when such a validator is called,
 * implementations don't have to worry about the validity of their data. They
 * just have to concentrate on validating their input.</p>
 *
 * @see SyntaxValidator
 * @see KeywordFactory
 */
public abstract class KeywordValidator
    implements Validator
{
    /**
     * The keyword
     */
    protected final String keyword;

    protected KeywordValidator(final String keyword)
    {
        this.keyword = keyword;
    }
}
