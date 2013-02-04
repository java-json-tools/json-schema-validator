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

package com.github.fge.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processing.keyword.KeywordDescriptor;
import com.github.fge.jsonschema.util.JsonLoader;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertNotNull;

public abstract class AbstractKeywordValidatorTest
{
    protected final Dictionary<KeywordDescriptor> dict;
    protected final String keyword;
    protected final KeywordDescriptor descriptor;
    protected final JsonNode data;

    protected AbstractKeywordValidatorTest(
        final Dictionary<KeywordDescriptor> dict, final String prefix,
        final String keyword)
        throws IOException
    {
        this.dict = dict;
        this.keyword = keyword;
        descriptor = dict.get(keyword);
        final String resourceName = String.format("/keyword/%s/%s.json",
            prefix, keyword);
        data = JsonLoader.fromResource(resourceName);
    }

    @Test
    public final void keywordExists()
    {
        assertNotNull(descriptor, "no support for " + keyword + "??");
    }
}
