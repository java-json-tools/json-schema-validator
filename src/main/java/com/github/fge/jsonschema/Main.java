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

package com.github.fge.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ProcessingReport;

import java.io.IOException;

/**
 * Basic schema validation via the command line
 *
 * The main method expects 2 arguments: a path to a schema and a path to an 
 * instance to be validated against the schema. It the validates it and prints
 * a report.
 *
 */
public final class Main
{
    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        // validate args
        String usage = "Validator needs a schema path and an instance path as arguments";
        if (args.length != 2){
            System.out.println(usage);
            System.exit(1);
        }
        
        // get args
        String schemaPath = args[0];
        String instancePath = args[1];
        
        // tell the user what they gave us
        System.out.println("Using schema at " + schemaPath);
        System.out.println("Using instance at " + instancePath);
        
        // load the files
        final JsonNode schemaNode = JsonLoader.fromPath(schemaPath);
        final JsonNode instance = JsonLoader.fromPath(instancePath);

        // create the schema
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema = factory.getJsonSchema(schemaNode);

        // validate and print
        ProcessingReport report = schema.validate(instance);
        System.out.println(report);

    }
}
