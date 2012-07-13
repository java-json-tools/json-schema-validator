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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.ValidationContext;
import org.eel.kitchen.jsonschema.bundle.Keyword;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.util.CollectionUtils;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Factory to provide a set of {@link KeywordValidator} instances for a given
 * schema
 *
 * <p>This class is only called once the schemas has been deemed valid,
 * that is, after the following items all stand true:</p>
 *
 * <ul>
 *     <li>the JSON document is not a JSON reference (ie, if it was,
 *     it has been resolved successfully);</li>
 *     <li>it is syntactically valid (see {@link SyntaxValidator}).</li>
 * </ul>
 */
public final class KeywordFactory
{
    /**
     * Our existing set of keyword validators
     */
    private final Map<String, Class<? extends KeywordValidator>>
        validators = new HashMap<String, Class<? extends KeywordValidator>>();

    /**
     * The only constructor
     *
     * @param bundle The keyword bundle to use
     */
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

    /**
     * Return the set of validators for a particular schema
     *
     * @param schema the schema as a {@link JsonNode}
     * @return the set of validators
     */
    public Set<KeywordValidator> getValidators(final JsonNode schema)
    {
        final Set<KeywordValidator> ret = new HashSet<KeywordValidator>();

        final Set<String> set = CollectionUtils.toSet(schema.fieldNames());

        set.retainAll(validators.keySet());

        for (final String keyword: set)
            ret.add(buildValidator(validators.get(keyword), schema));

        return ret;
    }

    /**
     * Build one validator
     *
     * <p>This is done by reflection. Remember that the contract is to have a
     * constructor which takes a {@link JsonNode} as an argument.
     * </p>
     *
     * <p>If instantiation fails for whatever reason,
     * an "invalid validator" is returned which always fails</p>
     *
     * @see #invalidValidator(Exception)
     *
     * @param c the keyword validator class
     * @param schema the schema
     * @return the instantiated keyword validator
     */
    private KeywordValidator buildValidator(
        final Class<? extends KeywordValidator> c, final JsonNode schema)
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

    /**
     * Build an invalid validator in the event of instantiation failure
     *
     * @param e the exception raised by the instantiation attempt
     * @return a keyword validator which always fails
     */
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
