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

package eel.kitchen.jsonschema.factories;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.jsonschema.keyword.format.AbstractFormatValidator;
import eel.kitchen.jsonschema.keyword.format.CSSColorValidator;
import eel.kitchen.jsonschema.keyword.format.CSSStyleValidator;
import eel.kitchen.jsonschema.keyword.format.DateFormatValidator;
import eel.kitchen.jsonschema.keyword.format.DateTimeFormatValidator;
import eel.kitchen.jsonschema.keyword.format.EmailFormatValidator;
import eel.kitchen.jsonschema.keyword.format.HostnameValidator;
import eel.kitchen.jsonschema.keyword.format.IPV4Validator;
import eel.kitchen.jsonschema.keyword.format.IPV6Validator;
import eel.kitchen.jsonschema.keyword.format.PhoneNumberValidator;
import eel.kitchen.jsonschema.keyword.format.RegexValidator;
import eel.kitchen.jsonschema.keyword.format.TimeFormatValidator;
import eel.kitchen.jsonschema.keyword.format.URIValidator;
import eel.kitchen.jsonschema.keyword.format.UnixEpochValidator;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static eel.kitchen.util.NodeType.*;

public final class FormatFactory
{
    private final Map<String, EnumSet<NodeType>> typeMap
        = new HashMap<String, EnumSet<NodeType>>();

    private final Map<String, Class<? extends Validator>> validators
        = new HashMap<String, Class<? extends Validator>>();

    private final ValidationReport report;

    public FormatFactory(final ValidationContext context)
    {
        report = context.createReport();

        registerFormat("date-time", DateTimeFormatValidator.class, STRING);
        registerFormat("date", DateFormatValidator.class, STRING);
        registerFormat("time", TimeFormatValidator.class, STRING);
        registerFormat("utc-millisec", UnixEpochValidator.class, INTEGER, NUMBER);
        registerFormat("regex", RegexValidator.class, STRING);
        registerFormat("color", CSSColorValidator.class, STRING);
        registerFormat("style", CSSStyleValidator.class, STRING);
        registerFormat("phone", PhoneNumberValidator.class, STRING);
        registerFormat("uri", URIValidator.class, STRING);
        registerFormat("email", EmailFormatValidator.class, STRING);
        registerFormat("ip-address", IPV4Validator.class, STRING);
        registerFormat("ipv6", IPV6Validator.class, STRING);
        registerFormat("host-name", HostnameValidator.class, STRING);
    }

    public Validator getFormatValidator(final String name, final JsonNode node)
    {
        final NodeType type = NodeType.getNodeType(node);

        if (!typeMap.containsKey(name)) {
            report.addMessage("no validator for format " + name);
            return new AlwaysFalseValidator(report);
        }

        if (!typeMap.get(name).contains(type))
            return new AlwaysTrueValidator(report, node);

        return doGetValidator(name, node);
    }

    private Validator doGetValidator(final String name, final JsonNode node)
    {
        final Class<? extends Validator> c = validators.get(name);

        final Constructor<? extends Validator> constructor;

        try {
            constructor = c.getConstructor(ValidationReport.class,
                JsonNode.class);
            return constructor.newInstance(report, node);
        } catch (Exception e) {
            final String msg = String.format("cannot instantiate format "
                + "validator for %s: %s: %s", name, e.getClass().getName(),
                e.getMessage());
            report.addMessage(msg);
            return new AlwaysFalseValidator(report);
        }
    }


    private void registerFormat(final String name,
        final Class<? extends AbstractFormatValidator> c,
        final NodeType... types)
    {
        final EnumSet<NodeType> typeSet
            = EnumSet.copyOf(Arrays.asList(types));

        typeMap.put(name, typeSet);
        validators.put(name, c);
    }
}
