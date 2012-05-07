/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.format.specifiers.CSSColorValidator;
import org.eel.kitchen.jsonschema.format.specifiers.CSSStyleValidator;
import org.eel.kitchen.jsonschema.format.specifiers.DateFormatValidator;
import org.eel.kitchen.jsonschema.format.specifiers.DateTimeFormatValidator;
import org.eel.kitchen.jsonschema.format.specifiers.EmailFormatValidator;
import org.eel.kitchen.jsonschema.format.specifiers.FormatValidator;
import org.eel.kitchen.jsonschema.format.specifiers.HostnameValidator;
import org.eel.kitchen.jsonschema.format.specifiers.IPV4Validator;
import org.eel.kitchen.jsonschema.format.specifiers.IPV6Validator;
import org.eel.kitchen.jsonschema.format.specifiers.PhoneNumberValidator;
import org.eel.kitchen.jsonschema.format.specifiers.RegexValidator;
import org.eel.kitchen.jsonschema.format.specifiers.TimeFormatValidator;
import org.eel.kitchen.jsonschema.format.specifiers.URIValidator;
import org.eel.kitchen.jsonschema.format.specifiers.UnixEpochValidator;
import org.eel.kitchen.util.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static org.eel.kitchen.util.NodeType.*;

/**
 * A factory spawning validators for the {@code format} keyword.
 */
public final class FormatFactory
{
    /**
     * The one and only validator factory
     */
    private static final FormatFactory instance = new FormatFactory();

    private FormatFactory()
    {
        registerFormat("date-time", new DateTimeFormatValidator(), STRING);
        registerFormat("date", new DateFormatValidator(), STRING);
        registerFormat("time", new TimeFormatValidator(), STRING);
        registerFormat("utc-millisec", new UnixEpochValidator(), INTEGER,
            NUMBER);
        registerFormat("regex", new RegexValidator(), STRING);
        registerFormat("color", new CSSColorValidator(), STRING);
        registerFormat("style", new CSSStyleValidator(), STRING);
        registerFormat("phone", new PhoneNumberValidator(), STRING);
        registerFormat("uri", new URIValidator(), STRING);
        registerFormat("email", new EmailFormatValidator(), STRING);
        registerFormat("ip-address", new IPV4Validator(), STRING);
        registerFormat("ipv6", new IPV6Validator(), STRING);
        registerFormat("host-name", new HostnameValidator(), STRING);
    }

    public static FormatFactory getInstance()
    {
        return instance;
    }

    /**
     * Our logger
     */
    private static final Logger logger
        = LoggerFactory.getLogger(FormatFactory.class);

    /**
     * Map pairing a format specification with the list of instance types
     * they support
     */
    private final Map<String, EnumSet<NodeType>> typeMap
        = new HashMap<String, EnumSet<NodeType>>();

    /**
     * Map pairing a format specification with the corresponding validator
     */
    private final Map<String, FormatValidator> validators
        = new HashMap<String, FormatValidator>();

    /**
     * Get the {@link Validator} for the given format specification and
     * instance
     *
     * <p>If the format specification is unknown,
     * the validation is ignored; if the format specification is
     * not applicable to the instance type, an {@link AlwaysTrueValidator} is
     * returned.</p>
     *
     * @param name the format specification
     * @param instance the instance to validate
     * @return the matching validator
     */
    public Validator getFormatValidator(final String name,
        final JsonNode instance)
    {
        final NodeType type = getNodeType(instance);

        if (!typeMap.containsKey(name)) {
            logger.warn("no validator for format \"{}\"", name);
            return new AlwaysTrueValidator();
        }

        if (!typeMap.get(name).contains(type))
            return new AlwaysTrueValidator();

        return validators.get(name);
    }

    /**
     * Register a format validator
     *
     * @param name the format name specification
     * @param validator the validator to register
     * @param types the types this format specification can validate
     */
    private void registerFormat(final String name,
        final FormatValidator validator, final NodeType... types)
    {
        final EnumSet<NodeType> typeSet = EnumSet.copyOf(Arrays.asList(types));

        typeMap.put(name, typeSet);
        validators.put(name, validator);
    }
}
