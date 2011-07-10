package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.Validator;

import java.util.HashMap;
import java.util.Map;

public class FormatValidatorFactory
{
    private final Map<String, Class<? extends Validator>> validators
        = new HashMap<String, Class<? extends Validator>>();

    public FormatValidatorFactory()
    {
        validators.put("date-time", ISO8601DateFormatValidator.class);
        validators.put("date", DateFormatValidator.class);
        validators.put("time", TimeFormatValidator.class);
        validators.put("utc-millisec", UnixEpochFormatValidator.class);
        validators.put("regex", RegexFormatValidator.class);
        validators.put("color", CSSColorValidator.class);
        validators.put("style", CSSStyleValidator.class);
        validators.put("phone", PhoneNumberFormatValidator.class);
        validators.put("uri", URIFormatValidator.class);
        validators.put("email", EmailFormatValidator.class);
        validators.put("ip-address", IPv4FormatValidator.class);
        validators.put("ipv6", IPv6FormatValidator.class);
        validators.put("host-name", HostnameFormatValidator.class);
    }
}
