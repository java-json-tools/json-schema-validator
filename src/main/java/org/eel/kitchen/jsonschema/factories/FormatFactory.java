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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.factories;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.base.AbstractValidator;
import org.eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.jsonschema.keyword.format.AlwaysFalseFormatValidator;
import org.eel.kitchen.jsonschema.keyword.format.AlwaysTrueFormatValidator;
import org.eel.kitchen.jsonschema.keyword.format.CSSColorValidator;
import org.eel.kitchen.jsonschema.keyword.format.CSSStyleValidator;
import org.eel.kitchen.jsonschema.keyword.format.DateFormatValidator;
import org.eel.kitchen.jsonschema.keyword.format.DateTimeFormatValidator;
import org.eel.kitchen.jsonschema.keyword.format.EmailFormatValidator;
import org.eel.kitchen.jsonschema.keyword.format.FormatValidator;
import org.eel.kitchen.jsonschema.keyword.format.HostnameValidator;
import org.eel.kitchen.jsonschema.keyword.format.IPV4Validator;
import org.eel.kitchen.jsonschema.keyword.format.IPV6Validator;
import org.eel.kitchen.jsonschema.keyword.format.PhoneNumberValidator;
import org.eel.kitchen.jsonschema.keyword.format.RegexValidator;
import org.eel.kitchen.jsonschema.keyword.format.TimeFormatValidator;
import org.eel.kitchen.jsonschema.keyword.format.URIValidator;
import org.eel.kitchen.jsonschema.keyword.format.UnixEpochValidator;
import org.eel.kitchen.util.NodeType;

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
     * Report fed to the matching validator (see {@link
     * #getFormatValidator(String, JsonNode)})
     */
    private final ValidationReport report;

    /**
     * Constructor
     *
     * @param context the {@link ValidationContext} for this factory
     */
    public FormatFactory(final ValidationContext context)
    {
        report = context.createReport();

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

    /**
     * Get the {@link Validator} for the given format specification and
     * instance. If the format specification is unknown,
     * an {@link AlwaysFalseValidator} is returned; if the format
     * specification is not applicable to the instance type,
     * {@link AbstractValidator#TRUE} is returned.
     *
     * @param name the format specification
     * @param node the instance to validate
     * @return the matching validator
     */
    public FormatValidator getFormatValidator(final String name,
        final JsonNode node)
    {
        final NodeType type = NodeType.getNodeType(node);

        if (!typeMap.containsKey(name)) {
            report.addMessage("no validator for format " + name);
            return new AlwaysFalseFormatValidator(report);
        }

        if (!typeMap.get(name).contains(type))
            return new AlwaysTrueFormatValidator();

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
        final EnumSet<NodeType> typeSet
            = EnumSet.copyOf(Arrays.asList(types));

        typeMap.put(name, typeSet);
        validators.put(name, validator);
    }
}
