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

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class FormatValidator
    extends AbstractValidator
{
    private static final Logger logger
        = LoggerFactory.getLogger(FormatValidator.class);

    private static final Map<NodeType, List<Class<? extends Validator>>> checkers
        = new EnumMap<NodeType, List<Class<? extends Validator>>>(NodeType.class);

    private String format;

    static {
        registerFormat(NodeType.STRING, CSSColorValidator.class);
        registerFormat(NodeType.STRING, CSSStyleValidator.class);
        registerFormat(NodeType.STRING, DateFormatValidator.class);
        registerFormat(NodeType.STRING, EmailFormatValidator.class);
        registerFormat(NodeType.STRING, HostnameFormatValidator.class);
        registerFormat(NodeType.STRING, IPv4FormatValidator.class);
        registerFormat(NodeType.STRING, IPv6FormatValidator.class);
        registerFormat(NodeType.STRING, ISO8601DateFormatValidator.class);
        registerFormat(NodeType.STRING, PhoneNumberFormatValidator.class);
        registerFormat(NodeType.STRING, RegexFormatValidator.class);
        registerFormat(NodeType.STRING, TimeFormatValidator.class);
        registerFormat(NodeType.INTEGER, UnixEpochFormatValidator.class);
        registerFormat(NodeType.NUMBER, UnixEpochFormatValidator.class);
        registerFormat(NodeType.STRING, URIFormatValidator.class);
    }

    public FormatValidator()
    {
        registerField("format", NodeType.STRING);
    }

    private static void registerFormat(final NodeType type,
        final Class<? extends Validator> validator)
    {
        if (!checkers.containsKey(type))
            checkers.put(type, new ArrayList<Class<? extends Validator>>());

        checkers.get(type).add(validator);
    }

    @Override
    protected boolean doSetup()
    {
        final JsonNode formatNode = schema.get("format");

        if (formatNode != null)
            format = formatNode.getTextValue();

        return true;
    }

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        if (format == null)
            return true;

        final NodeType nodeType = NodeType.getNodeType(node);

        if (!checkers.containsKey(nodeType)) {
            logger.warn("no format validators for node of type {}, "
                + "format validation ignored", nodeType);
            return true;
        }

        final String enumKey = format.replaceAll("-", "_").toUpperCase();
        final Class<? extends Validator> validatorClass;

        try {
            validatorClass = FormatPicker.valueOf(enumKey).getValidator();
        } catch (IllegalArgumentException e) {
            logger.error("No such format \"%s\", format validation ignored",
                format);
            return true;
        }

        if (!checkers.get(nodeType).contains(validatorClass)) {
            logger.warn("format \"{}\" cannot validate nodes of type \"{}\", "
                + "format validation ignored", format, nodeType);
            return true;
        }

        final Validator v;
        final boolean ret;

        try {
            v = validatorClass.getConstructor().newInstance();
        } catch (Exception e) {
            messages.add(String.format("cannot instantiate validator: "
                + "%s: %s", e.getClass().getCanonicalName(), e.getMessage()));
            return false;
        }

        ret = v.validate(node);
        messages.addAll(v.getMessages());
        return ret;
    }
}
