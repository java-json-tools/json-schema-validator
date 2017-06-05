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

package com.github.fge.jsonschema.keyword.validator.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.main.JsonSchema;
import com.google.common.collect.Lists;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonSchemaFactoryBuilder;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jackson.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class AllOfOverrideValidatorTest{
     @Test
    public final void testThatObjectOverrideIsApplied() throws Exception
    {
	    LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace("resource:/keyword/validators/draftv4/").freeze();
	    JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
	    JsonSchema schema = factory.getJsonSchema("allOf.json");
	    ProcessingReport report = schema.validate(JsonLoader.fromResource("/object/allOf_object.json"));
	    for(Object m : report){
		    System.out.println(m.toString());
	    }
	    assertTrue(report.isSuccess());
    }
}
