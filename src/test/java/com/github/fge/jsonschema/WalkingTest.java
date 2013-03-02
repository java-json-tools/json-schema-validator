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

package com.github.fge.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.walk.DraftV4PointerCollectorDictionary;
import com.github.fge.jsonschema.walk.PointerCollector;
import com.github.fge.jsonschema.walk.SchemaListener;
import com.github.fge.jsonschema.walk.SchemaWalker;
import com.github.fge.jsonschema.walk.SimpleSchemaWalker;
import com.github.fge.jsonschema.report.ConsoleProcessingReport;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;

import static org.mockito.Mockito.*;

public final class WalkingTest
{
    private WalkingTest()
    {
    }

    public static void main(final String... args)
        throws ProcessingException
    {
        final ProcessingReport report = new ConsoleProcessingReport(
            LogLevel.DEBUG, LogLevel.FATAL);
        final SchemaListener listener = mock(SchemaListener.class);
        final JsonNode schema = SchemaVersion.DRAFTV4.getSchema();
        final Dictionary<PointerCollector> dict
            = DraftV4PointerCollectorDictionary.get();
        final SchemaWalker walker = new SimpleSchemaWalker(dict,
            new CanonicalSchemaTree(schema));

        walker.walk(listener, report);
    }
}
