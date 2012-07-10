/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.bundle.Keyword;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.bundle.KeywordBundles;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class KeywordFactory
{
    private final Map<String, Class<? extends KeywordValidator>>
        validators = new HashMap<String, Class<? extends KeywordValidator>>();

    public KeywordFactory()
    {
        this(KeywordBundles.defaultBundle());
    }

    public KeywordFactory(final KeywordBundle bundle)
    {
        String keyword;
        Class<? extends KeywordValidator> validatorClass;

        for (final Map.Entry<String, Keyword> entry: bundle) {
            keyword = entry.getKey();
            validatorClass = entry.getValue().getValidatorClass();
            if (validatorClass != null)
                validators.put(keyword, validatorClass);
        }
    }

    public Set<KeywordValidator> getValidators(final JsonNode schema)
    {
        final Set<KeywordValidator> ret = new HashSet<KeywordValidator>();

        final Set<String> set = CollectionUtils.toSet(schema.fieldNames());

        set.retainAll(validators.keySet());

        for (final String keyword: set)
            ret.add(buildValidator(validators.get(keyword), schema));

        return ret;
    }

    private KeywordValidator buildValidator(final Class<? extends KeywordValidator> c,
        final JsonNode schema)
    {
        final Constructor<? extends KeywordValidator> constructor;

        try {
            constructor = c.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException e) {
            return invalidValidator(e);
        }

        try {
            return constructor.newInstance(schema);
        } catch (InstantiationException e) {
            return invalidValidator(e);
        } catch (IllegalAccessException e) {
            return invalidValidator(e);
        } catch (InvocationTargetException e) {
            return invalidValidator(e);
        }
    }

    private KeywordValidator invalidValidator(final Exception e)
    {
        return new KeywordValidator(NodeType.values())
        {
            @Override
            protected void validate(final ValidationContext context,
                final JsonNode instance)
            {
                context.addMessage("cannot build validator: "
                    + e.getClass().getName() + ": " + e.getMessage());
            }
        };
    }
}
