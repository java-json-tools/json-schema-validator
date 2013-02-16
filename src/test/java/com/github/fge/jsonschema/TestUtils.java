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

import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import org.mockito.verification.VerificationMode;

import static org.mockito.Mockito.*;

public final class TestUtils
{
    private TestUtils()
    {
    }

    public static VerificationMode onlyOnce()
    {
        return times(1);
    }

    public static ProcessingReport anyReport()
    {
        return any(ProcessingReport.class);
    }

    public static SchemaTree anySchema()
    {
        return any(SchemaTree.class);
    }

    public static ProcessingMessage anyMessage()
    {
        return any(ProcessingMessage.class);
    }
}
