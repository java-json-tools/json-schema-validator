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

package com.github.fge.jsonschema.util;

import com.google.common.collect.Sets;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class CharMatchersTest
{
    @DataProvider
    private Iterator<Object[]> notAToken()
    {
        final String s = "()<>@,;:\\\"/[]?= \b\f\n\r\t";
        final Set<Object[]> set = Sets.newHashSet();

        for (final char c: s.toCharArray())
            set.add(new Object[]{ c });

        return set.iterator();
    }

    @Test(dataProvider = "notAToken")
    public void RFC2045NonTokenIsAcknowledgedAsSuch(final char c)
    {
        assertFalse(CharMatchers.RFC2045_TOKEN.matches(c));
    }
}
