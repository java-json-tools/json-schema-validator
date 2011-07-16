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

/**
 * Helper enum for the {@link FormatValidator} class to pick an enum out of a
 * format string. Sketchy, should probably be replaced with a factory of some
 * sort, especially since it is non extensible - and the spec says you MAY
 * create custom formats.
 */
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

    private final Class<? extends Validator> v;

    FormatPicker(final Class<? extends Validator> v)
    {
        this.v = v;
    }

    public Class<? extends Validator> getValidator()
    {
        return v;
    }
}
