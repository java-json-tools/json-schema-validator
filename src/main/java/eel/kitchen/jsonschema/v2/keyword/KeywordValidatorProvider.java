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

import eel.kitchen.jsonschema.v2.schema.Schema;
import eel.kitchen.jsonschema.v2.schema.ValidationState;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class KeywordValidatorProvider
{
    private static final KeywordValidatorProvider instance
        = new KeywordValidatorProvider();

    private static final Map<String, Class<? extends KeywordValidator>> validators
        = new HashMap<String, Class<? extends KeywordValidator>>();

    private static final Map<String, EnumSet<NodeType>> typeMap
        = new HashMap<String, EnumSet<NodeType>>();

    static {
        registerValidator("minimum", MinimumKeywordValidator.class,
            NodeType.INTEGER, NodeType.NUMBER);
        registerValidator("maximum", MaximumKeywordValidator.class,
            NodeType.INTEGER, NodeType.NUMBER);
        registerValidator("divisibleBy", DivisibleByKeywordValidator.class,
            NodeType.INTEGER, NodeType.NUMBER);
        registerValidator("enum", EnumKeywordValidator.class, NodeType.values());
    }

    private static void registerValidator(final String field,
        final Class<? extends KeywordValidator> validator,
        final NodeType... types)
    {
        validators.put(field, validator);
        typeMap.put(field, EnumSet.copyOf(Arrays.asList(types)));
    }

    private KeywordValidatorProvider()
    {
    }

    public static KeywordValidatorProvider getInstance()
    {
        return instance;
    }

    public Set<KeywordValidator> getValidators(final JsonNode schema,
        final NodeType type)
    {
        final Set<KeywordValidator> ret = new HashSet<KeywordValidator>();

        final Iterator<String> fields = schema.getFieldNames();

        String field;
        EnumSet<NodeType> types;
        Class<? extends KeywordValidator> c;
        Constructor<? extends KeywordValidator> constructor;
        KeywordValidator validator;

        while (fields.hasNext()) {
            field = fields.next();
            types = typeMap.get(field);
            if (types == null)
                continue;
            if (!types.contains(type))
                continue;
            c = validators.get(field);

            try {
                constructor = c.getConstructor(JsonNode.class);
                validator = constructor.newInstance(schema);
                ret.add(validator);
            } catch (InvocationTargetException e) {
                return failure(field, e);
            } catch (NoSuchMethodException e) {
                return failure(field, e);
            } catch (InstantiationException e) {
                return failure(field, e);
            } catch (IllegalAccessException e) {
                return failure(field, e);
            }
        }

        return ret;
    }

    private static Set<KeywordValidator> failure(final String field,
        final Exception e)
    {
        final String message = String.format("failed to instantiate validator"
            + " for keyword %s: %s: %s", field, e.getClass().getCanonicalName(),
            e.getMessage());

        final KeywordValidator v = new KeywordValidator()
        {
            @Override
            public void validate(final ValidationState state,
                final JsonNode node)
            {
                state.addMessage(message);
                state.setStatus(ValidationStatus.FAILURE);
            }
        };

        return new HashSet<KeywordValidator>(Arrays.asList(v));
    }
}
