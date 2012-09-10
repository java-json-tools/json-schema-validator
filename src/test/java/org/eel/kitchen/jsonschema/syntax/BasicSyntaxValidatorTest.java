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

package org.eel.kitchen.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.Lists;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.bundle.KeywordBundles;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public final class BasicSyntaxValidatorTest
{
    @Test
    public void syntaxCheckingCorrectlyBalksOnNonObject()
    {
        final JsonNode wrong = JsonNodeFactory.instance.nullNode();
        final KeywordBundle bundle = KeywordBundles.defaultBundle();
        final SyntaxValidator validator = new SyntaxValidator(bundle);

        final List<ValidationMessage> messages = Lists.newArrayList();

        validator.validate(messages, wrong);

        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0).getInfo("found").textValue(),
            NodeType.NULL.toString());
    }
}
