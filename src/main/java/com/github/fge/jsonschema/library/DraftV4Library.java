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

import com.github.fge.jsonschema.core.keyword.syntax.dictionaries.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.library.digest.DraftV4DigesterDictionary;
import com.github.fge.jsonschema.library.format.DraftV4FormatAttributesDictionary;
import com.github.fge.jsonschema.library.validator.DraftV4ValidatorDictionary;

/**
 * Library of all draft v4 core schema keywords and format attributes
 */
public final class DraftV4Library
{
    private static final Library LIBRARY = new Library(
        DraftV4SyntaxCheckerDictionary.get(),
        DraftV4DigesterDictionary.get(),
        DraftV4ValidatorDictionary.get(),
        DraftV4FormatAttributesDictionary.get()
    );

    private DraftV4Library()
    {
    }

    public static Library get()
    {
        return LIBRARY;
    }
}
