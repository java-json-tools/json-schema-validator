package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class FormatValidatorTest
{
    private JsonNode schemas, inputs, node;
    private Validator v;
    private static final JsonNode dummy;

    static {
        try {
            dummy = new ObjectMapper().readTree("{}");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @BeforeClass
    public void setUp()
        throws IOException
    {
        inputs = JasonHelper.load("format.json");
        schemas = inputs.get("schemas");
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^format node is not a string$"
    )
    public void testBadFormat()
        throws MalformedJasonSchemaException
    {
        new FormatValidator(schemas.get("badformat")).setup();
    }

    @Test
    public void testTypeMismatch()
        throws MalformedJasonSchemaException
    {
        node = schemas.get("typemismatch");
        v = new FormatValidator(node.get("schema"));
        v.setup();

        assertTrue(v.validate(node.get("node")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testNoFormats()
        throws MalformedJasonSchemaException
    {
        node = schemas.get("noformats");
        v = new FormatValidator(node.get("schema"));
        v.setup();

        assertTrue(v.validate(node.get("node")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testDateTime()
    {
        node = inputs.get("date-time");
        v = new ISO8601DateFormatValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testDate()
    {
        node = inputs.get("date");
        v = new DateFormatValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testTime()
    {
        node = inputs.get("time");
        v = new TimeFormatValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testUTCMilliSec()
    {
        node = inputs.get("utc-millisec");
        v = new UnixEpochFormatValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testRegex()
    {
        node = inputs.get("regex");
        v = new RegexFormatValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testCSSColor()
    {
        node = inputs.get("color");
        v = new CSSColorValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testCSSStyle()
    {
        node = inputs.get("style");
        v = new CSSStyleValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testPhoneNumber()
    {
        node = inputs.get("phone");
        v = new PhoneNumberFormatValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testURI()
    {
        node = inputs.get("uri");
        v = new URIFormatValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testEmail()
    {
        node = inputs.get("email");
        v = new EmailFormatValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testIPv4()
    {
        node = inputs.get("ip-address");
        v = new IPv4FormatValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testIPv6()
    {
        node = inputs.get("ipv6");
        v = new IPv6FormatValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }

    @Test
    public void testHostName()
    {
        node = inputs.get("hostname");
        v = new HostnameFormatValidator(dummy);

        assertFalse(v.validate(node.get("bad")));
        assertTrue(v.validate(node.get("good")));
        assertTrue(v.getMessages().isEmpty());
    }
}


