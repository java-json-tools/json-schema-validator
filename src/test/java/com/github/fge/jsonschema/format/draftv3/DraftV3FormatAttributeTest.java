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

package com.github.fge.jsonschema.format.draftv3;

import com.github.fge.jsonschema.format.AbstractFormatAttributeTest;
import com.github.fge.jsonschema.metaschema.KeywordRegistries;

import java.io.IOException;

public abstract class DraftV3FormatAttributeTest
    extends AbstractFormatAttributeTest
{
    protected DraftV3FormatAttributeTest(final String fmt)
        throws IOException
    {
        super(KeywordRegistries.draftV3Core(), "draftv3", fmt);
    }
}
