package eel.kitchen.jsonschema.validators.format;


import eel.kitchen.jsonschema.validators.Validator;

public enum FormatPicker
{
    DATE_TIME(ISO8601DateFormatValidator.class),
    DATE(DateFormatValidator.class),
    TIME(TimeFormatValidator.class),
    UTC_MILLISEC(UnixEpochFormatValidator.class),
    REGEX(RegexFormatValidator.class),
    COLOR(CSSColorValidator.class),
    STYLE(CSSStyleValidator.class),
    PHONE(PhoneNumberFormatValidator.class),
    URI(URIFormatValidator.class),
    EMAIL(EmailFormatValidator.class),
    IP_ADDRESS(IPv4FormatValidator.class),
    IPV6(IPv6FormatValidator.class),
    HOST_NAME(HostnameFormatValidator.class);

    private Class<? extends Validator> v;

    FormatPicker(final Class<? extends Validator> v)
    {
        this.v = v;
    }

    public Class<? extends Validator> getValidator()
    {
        return v;
    }
}
