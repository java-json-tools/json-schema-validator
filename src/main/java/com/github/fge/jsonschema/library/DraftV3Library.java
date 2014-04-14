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

package com.github.fge.jsonschema.library;

import com.github.fge.jsonschema.core.keyword.syntax.dictionaries.DraftV3SyntaxCheckerDictionary;
import com.github.fge.jsonschema.library.digest.DraftV3DigesterDictionary;
import com.github.fge.jsonschema.library.format.DraftV3FormatAttributesDictionary;
import com.github.fge.jsonschema.library.validator.DraftV3ValidatorDictionary;

/**
 * Library of all draft v3 core schema keywords and format attributes
 */
public final class DraftV3Library
{
    private static final Library LIBRARY = new Library(
        DraftV3SyntaxCheckerDictionary.get(),
        DraftV3DigesterDictionary.get(),
        DraftV3ValidatorDictionary.get(),
        DraftV3FormatAttributesDictionary.get()
    );

    private DraftV3Library()
    {
    }

    public static Library get()
    {
        return LIBRARY;
    }
}
