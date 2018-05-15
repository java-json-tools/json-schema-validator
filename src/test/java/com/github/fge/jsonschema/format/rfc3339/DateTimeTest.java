/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.format.rfc3339;

import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.format.AbstractFormatAttributeTest;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.format.common.RFC3339DateTimeAttribute;

import java.io.IOException;
import java.text.Format;

public final class DateTimeTest
    extends AbstractFormatAttributeTest
{
    private static final Dictionary<FormatAttribute> dict =
        Dictionary
            .<FormatAttribute>newBuilder()
            .addEntry("date-time", RFC3339DateTimeAttribute.getInstance())
            .freeze();

    public DateTimeTest()
        throws IOException
    {
        super(dict, "rfc3339", "date-time");
    }
}
