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

package org.eel.kitchen.jsonschema.util;

import com.google.common.base.CharMatcher;

/**
 * Utility class to match an RFC 2045 token
 *
 * <p>This class is a {@link CharMatcher} which recognizes what is called a
 * token in <a href="http://tools.ietf.org/html/rfc2045#section-6.1">RFC 2045,
 * section 6.1</a>.</p>
 */
public final class RFC2045TokenMatcher
    extends CharMatcher
{
    private static final CharMatcher INSTANCE = new RFC2045TokenMatcher();

    private final CharMatcher matcher;

    private RFC2045TokenMatcher()
    {
        final CharMatcher tspecial = CharMatcher.anyOf("()<>@,;:\\\"/[]?=");
        final CharMatcher ctlOrSpace
            = CharMatcher.JAVA_ISO_CONTROL.or(CharMatcher.WHITESPACE);
        final CharMatcher notToken = tspecial.or(ctlOrSpace).negate();

        matcher = CharMatcher.ASCII.and(notToken).precomputed();
    }

    public static CharMatcher getInstance()
    {
        return INSTANCE;
    }

    @Override
    public boolean matches(final char c)
    {
        return matcher.matches(c);
    }

    @Override
    public String toString()
    {
        return "RFC 2045 token matcher";
    }
}
