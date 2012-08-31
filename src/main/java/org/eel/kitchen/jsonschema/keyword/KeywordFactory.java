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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.report.ValidationDomain;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Factory to provide a set of {@link KeywordValidator} instances for a given
 * schema
 *
 * <p>This class is only called once the schemas has been deemed valid,
 * that is, after the following is true:</p>
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
    private final Map<String, Class<? extends KeywordValidator>> validators;

    /**
     * The only constructor
     *
     * @param bundle The keyword bundle to use
     */
    public KeywordFactory(final KeywordBundle bundle)
    {
        validators = ImmutableMap.copyOf(bundle.getValidators());
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

        final Set<String> set = JacksonUtils.fieldNames(schema);

        set.retainAll(validators.keySet());

        for (final String keyword: set)
            ret.add(buildValidator(validators.get(keyword), schema));

        return ImmutableSet.copyOf(ret);
    }

    /**
     * Build one validator
     *
     * <p>This is done by reflection. Remember that the contract is to have a
     * constructor which takes a {@link JsonNode} as an argument.
     * </p>
     *
     * <p>If instantiation fails for whatever reason, an "invalid validator" is
     * returned which always fails.</p>
     *
     * @see #invalidValidator(Class, Exception)
     *
     * @param c the keyword validator class
     * @param schema the schema
     * @return the instantiated keyword validator
     */
    private static KeywordValidator buildValidator(
        final Class<? extends KeywordValidator> c, final JsonNode schema)
    {
        final Constructor<? extends KeywordValidator> constructor;

        try {
            constructor = c.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException e) {
            return invalidValidator(c, e);
        }

        try {
            return constructor.newInstance(schema);
        } catch (InstantiationException e) {
            return invalidValidator(c, e);
        } catch (IllegalAccessException e) {
            return invalidValidator(c, e);
        } catch (InvocationTargetException e) {
            return invalidValidator(c, e);
        }
    }

    /**
     * Build an invalid validator in the event of instantiation failure
     *
     * @param e the exception raised by the instantiation attempt
     * @return a keyword validator which always fails
     */
    private static KeywordValidator invalidValidator(
        final Class<? extends KeywordValidator> c, final Exception e)
    {
        final String className = c.getClass().getName();

        return new KeywordValidator(className, NodeType.values())
        {
            @Override
            protected void validate(final ValidationContext context,
                final ValidationReport report, final JsonNode instance)
            {
                final ValidationMessage.Builder msg
                    = new ValidationMessage.Builder(ValidationDomain.VALIDATION)
                        .setMessage("cannot build validator")
                        .addInfo("exception", e.getClass().getName())
                        .addInfo("exceptionMessage", e.getMessage());
                report.addMessage(msg.build());
            }

            @Override
            public String toString()
            {
                return className;
            }
        };
    }
}
