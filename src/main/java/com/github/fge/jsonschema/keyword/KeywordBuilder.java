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

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;

public final class KeywordBuilder
    implements Processor<ValidationData, KeywordSet>
{
    private final Dictionary<KeywordDescriptor> dict;

    public KeywordBuilder(final Dictionary<KeywordDescriptor> dict)
    {
        this.dict = dict;
    }

    /**
     * Process the input
     *
     * @param report the report to use while processing
     * @param input the input for this processor
     * @return the output
     * @throws ProcessingException processing failed
     */
    @Override
    public KeywordSet process(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
