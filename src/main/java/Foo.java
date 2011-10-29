/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.net.URI;
import java.net.URISyntaxException;

public final class Foo
{
    public static void main(final String... args)
        throws URISyntaxException
    {
        final URI uri = new URI(null, null, null, "/my fragment");

        System.out.println(uri.toASCIIString());
        System.out.println(uri.toString());
        System.out.println(uri.getFragment());
    }
}
