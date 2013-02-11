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

package com.github.fge.jsonschema.util;

import com.google.common.base.CharMatcher;

/**
 * A collection of dedicated {@link CharMatcher}s
 */
public final class CharMatchers
{
    private CharMatchers()
    {
    }

    /**
     * Match lowercase US ASCII letters
     */
    public static final CharMatcher LOALPHA;

    /**
     * Match any character valid in a link relation apart from the first one
     */
    public static final CharMatcher REL_TOKEN;

    /**
     * Match any character valid as a token by RFC 2045
     *
     * <p>This matches any character as defined by the {@code token} grammar
     * specification in RFC 2045, section 5.1.</p>
     */
    public static final CharMatcher RFC2045_TOKEN;

    static {
        final CharMatcher loAlpha = CharMatcher.inRange('a', 'z');
        final CharMatcher relToken = loAlpha.or(CharMatcher.inRange('0', '9'))
            .or(CharMatcher.anyOf(".-"));

        LOALPHA = loAlpha.precomputed();
        REL_TOKEN = relToken.precomputed();

        final CharMatcher tspecial = CharMatcher.anyOf("()<>@,;:\\\"/[]?=");
        final CharMatcher ctlOrSpace
            = CharMatcher.JAVA_ISO_CONTROL.or(CharMatcher.WHITESPACE);
        final CharMatcher notToken = tspecial.or(ctlOrSpace).negate();

        RFC2045_TOKEN = CharMatcher.ASCII.and(notToken).precomputed();
    }
}
