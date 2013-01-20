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

package com.github.fge.jsonschema.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.JsonLoader;

import java.io.IOException;

/**
 * Base abstract class for all examples
 */
public abstract class ExampleBase
{
    private static final String PKGBASE;
    private static final ObjectWriter WRITER;

    static {
        final String pkgName = ExampleBase.class.getPackage().getName();
        PKGBASE = '/' + pkgName.replace(".", "/");
        WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();
    }

    /**
     * Pretty print a validation report to stdout
     *
     * <p>Will print whether validation succeeded. In the event of a failure,
     * dumps validation error messages.</p>
     *
     * @param report the report
     * @throws IOException failure to print to stdout
     */
    protected static void printReport(final ValidationReport report)
        throws IOException
    {
        final boolean success = report.isSuccess();
        System.out.println("Validation " + (success ? "succeeded" : "failed"));

        if (!success) {
            System.out.println("---- BEGIN REPORT ----");
            System.out.println(WRITER.writeValueAsString(report.asJsonObject()));
            System.out.println("---- END REPORT ----");
        }
    }

    /**
     * Load one resource from the current package as a {@link JsonNode}
     *
     * @param name name of the resource (<b>MUST</b> start with {@code /}
     * @return a JSON document
     * @throws IOException resource not found
     */
    protected static JsonNode loadResource(final String name)
        throws IOException
    {
        return JsonLoader.fromResource(PKGBASE + name);
    }
}
