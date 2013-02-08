/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.keyword.validator.callback.draftv3;

import com.github.fge.jsonschema.keyword.validator.callback.CallbackValidatorTest;
import com.github.fge.jsonschema.library.validator.DraftV3ValidatorDictionary;
import com.github.fge.jsonschema.ref.JsonPointer;

public abstract class DraftV3CallbackValidatorTest
    extends CallbackValidatorTest
{
    protected DraftV3CallbackValidatorTest(final String keyword,
        final JsonPointer ptr1, final JsonPointer ptr2)
    {
        super(DraftV3ValidatorDictionary.get(), keyword, ptr1, ptr2);
    }
}
