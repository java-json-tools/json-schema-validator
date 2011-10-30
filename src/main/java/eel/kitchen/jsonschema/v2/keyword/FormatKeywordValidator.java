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

package eel.kitchen.jsonschema.v2.keyword;

import eel.kitchen.jsonschema.v2.schema.ValidationState;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.jsonschema.validators.format.CSSColorValidator;
import eel.kitchen.jsonschema.validators.format.CSSStyleValidator;
import eel.kitchen.jsonschema.validators.format.DateFormatValidator;
import eel.kitchen.jsonschema.validators.format.EmailFormatValidator;
import eel.kitchen.jsonschema.validators.format.IPv4FormatValidator;
import eel.kitchen.jsonschema.validators.format.IPv6FormatValidator;
import eel.kitchen.jsonschema.validators.format.ISO8601DateFormatValidator;
import eel.kitchen.jsonschema.validators.format.PhoneNumberFormatValidator;
import eel.kitchen.jsonschema.validators.format.RegexFormatValidator;
import eel.kitchen.jsonschema.validators.format.TimeFormatValidator;
import eel.kitchen.jsonschema.validators.format.URIFormatValidator;
import eel.kitchen.jsonschema.validators.format.UnixEpochFormatValidator;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public final class FormatKeywordValidator
    extends AbstractKeywordValidator
{
    private static final Map<String, Class<? extends Validator>> validators
        = new HashMap<String, Class<? extends Validator>>();

    private static final Map<String, EnumSet<NodeType>> typeMap
        = new HashMap<String, EnumSet<NodeType>>();

    static {
        registerValidator("date-time", ISO8601DateFormatValidator.class,
            NodeType.STRING);
        registerValidator("date", DateFormatValidator.class, NodeType.STRING);
        registerValidator("time", TimeFormatValidator.class, NodeType.STRING);
        registerValidator("utc-millisec", UnixEpochFormatValidator.class,
            NodeType.INTEGER, NodeType.NUMBER);
        registerValidator("regex", RegexFormatValidator.class, NodeType.STRING);
        registerValidator("color", CSSColorValidator.class, NodeType.STRING);
        registerValidator("style", CSSStyleValidator.class, NodeType.STRING);
        registerValidator("phone", PhoneNumberFormatValidator.class,
            NodeType.STRING);
        registerValidator("uri", URIFormatValidator.class, NodeType.STRING);
        registerValidator("email", EmailFormatValidator.class, NodeType.STRING);
        registerValidator("ip-address", IPv4FormatValidator.class,
            NodeType.STRING);
        registerValidator("ipv6", IPv6FormatValidator.class, NodeType.STRING);
    }

    private static void registerValidator(final String field,
        final Class<? extends Validator> validator, final NodeType... types)
    {
        validators.put(field, validator);
        typeMap.put(field, EnumSet.copyOf(Arrays.asList(types)));
    }

    public FormatKeywordValidator(final JsonNode schema)
    {
        super(schema);
    }

    @Override
    public void validate(final ValidationState state, final JsonNode node)
    {
        final String format = schema.get("format").getTextValue();

        final EnumSet<NodeType> typeSet = typeMap.get(format);

        if (typeSet == null)
            return;

        if (!typeSet.contains(NodeType.getNodeType(node)))
            return;

        final Class<? extends Validator> c = validators.get(format);

        final Constructor<? extends Validator> constructor;

        try {
            constructor = c.getConstructor();
        } catch (NoSuchMethodException ignored) {
            state.addMessage("cannot instantiate format validator for " +
                format);
            return;
        }

        boolean oops = false;

        final Validator v;

        try {
            v = constructor.newInstance();
            if (!v.validate(node))
                state.addMessages(v.getMessages());
        } catch (InstantiationException ignored) {
            oops = true;
        } catch (IllegalAccessException ignored) {
            oops = true;
        } catch (InvocationTargetException ignored) {
            oops = true;
        }

        if (oops)
            state.addMessage("cannot instantiate format validator for " +
                format);
    }
}
