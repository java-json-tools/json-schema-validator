/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.*;

public class FormatValidatorTest
{
    private JsonNode schemas, inputs, node;
    private final Validator v = new FormatValidator();

    @BeforeClass
    public void setUp()
        throws IOException
    {
        inputs = JasonHelper.load("format.json");
        schemas = inputs.get("schemas");
    }

    @Test
    public void testBadFormat()
    {
        v.setSchema(schemas.get("badformat"));

        assertFalse(v.setup());

        final List<String> messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "format is of type boolean, "
            + "expected [string]");
    }

    @Test
    public void testTypeMismatch()
    {
        node = schemas.get("typemismatch");
        v.setSchema(node.get("schema"));

        assertTrue(v.setup());

        assertTrue(v.validate(node.get("node")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testNoFormats()
    {
        node = schemas.get("noformats");
        v.setSchema(node.get("schema"));

        assertTrue(v.setup());

        assertTrue(v.validate(node.get("node")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testDateTime()
    {
        node = inputs.get("date-time");
        final Validator validator = new ISO8601DateFormatValidator();

        assertFalse(validator.validate(node.get("bad")));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }

    @Test
    public void testDate()
    {
        node = inputs.get("date");
        final Validator validator = new DateFormatValidator();

        assertFalse(validator.validate(node.get("bad")));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }

    @Test
    public void testTime()
    {
        node = inputs.get("time");
        final Validator validator = new TimeFormatValidator();

        assertFalse(validator.validate(node.get("bad")));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }

    @Test
    public void testUTCMilliSec()
    {
        node = inputs.get("utc-millisec");
        final Validator validator = new UnixEpochFormatValidator();

        for (final JsonNode element: node.get("bad"))
            assertFalse(validator.validate(element));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }

    @Test
    public void testRegex()
    {
        node = inputs.get("regex");
        final Validator v = new RegexFormatValidator();

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testCSSColor()
    {
        node = inputs.get("color");
        final Validator validator = new CSSColorValidator();

        assertFalse(validator.validate(node.get("bad")));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }

    @Test
    public void testCSSStyle()
    {
        node = inputs.get("style");
        final Validator validator = new CSSStyleValidator();

        assertFalse(validator.validate(node.get("bad")));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }

    @Test
    public void testPhoneNumber()
    {
        node = inputs.get("phone");
        final Validator validator = new PhoneNumberFormatValidator();

        assertFalse(validator.validate(node.get("bad")));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }

    @Test
    public void testURI()
    {
        node = inputs.get("uri");
        final Validator validator = new URIFormatValidator();

        assertFalse(validator.validate(node.get("bad")));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }

    @Test
    public void testEmail()
    {
        node = inputs.get("email");
        final Validator validator = new EmailFormatValidator();

        assertFalse(validator.validate(node.get("bad")));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }

    @Test
    public void testIPv4()
    {
        node = inputs.get("ip-address");
        final Validator validator = new IPv4FormatValidator();

        for (final JsonNode element: node.get("bad"))
            assertFalse(validator.validate(element));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }

    @Test
    public void testIPv6()
    {
        node = inputs.get("ipv6");
        final Validator validator = new IPv6FormatValidator();

        for (final JsonNode element: node.get("bad"))
            assertFalse(validator.validate(element));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }

    @Test
    public void testHostName()
    {
        node = inputs.get("hostname");
        final Validator validator = new HostnameFormatValidator();

        for (final JsonNode element: node.get("bad"))
            assertFalse(validator.validate(element));
        assertTrue(validator.validate(node.get("good")));
        assertTrue(validator.getMessages().isEmpty());
    }
}

