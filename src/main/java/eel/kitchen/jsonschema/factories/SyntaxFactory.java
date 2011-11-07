/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
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
import eel.kitchen.jsonschema.base.MatchAllValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.jsonschema.syntax.*;
import eel.kitchen.jsonschema.syntax.PropertiesSyntaxValidator;
import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>Factory providing syntax checking validators for a schema.</p>
 *
 * <p>While the schema can validate itself, there is a chicken and egg
 * problem and we need to do this operation. The only user for this factory
 * is {@link ValidationContext#getValidator(JsonNode)}.
 * </p>
 *
 * <p>Note that unknown keywords to this factory trigger a validation
 * <b>failure</b>. Therefore, it is important that all keywords be
 * registered.</p>
 */
public final class SyntaxFactory
{
    /**
     * Map pairing a schema keyword with its corresponding syntax validator
     */
    private final Map<String, Class<? extends SyntaxValidator>> validators
        = new HashMap<String, Class<? extends SyntaxValidator>>();

    /**
     * Constructor, registering all validators with {@link
     * #registerValidator(String, Class)}
     */
    public SyntaxFactory()
    {
        registerValidator("additionalItems",
            AdditionalItemsSyntaxValidator.class);
        registerValidator("additionalProperties",
            AdditionalPropertiesSyntaxValidator.class);
        registerValidator("default", AlwaysTrueSyntaxValidator.class);
        registerValidator("dependencies", DependenciesSyntaxValidator.class);
        registerValidator("description", DescriptionSyntaxValidator.class);
        registerValidator("disallow", DisallowSyntaxValidator.class);
        registerValidator("divisibleBy", DivisibleBySyntaxValidator.class);
        registerValidator("$ref", DollarRefSyntaxValidator.class);
        registerValidator("$schema", DollarSchemaSyntaxValidator.class);
        registerValidator("enum", EnumSyntaxValidator.class);
        registerValidator("exclusiveMaximum",
            ExclusiveMaximumSyntaxValidator.class);
        registerValidator("exclusiveMinimum",
            ExclusiveMinimumSyntaxValidator.class);
        registerValidator("extends", ExtendsSyntaxValidator.class);
        registerValidator("format", FormatSyntaxValidator.class);
        registerValidator("id", IdSyntaxValidator.class);
        registerValidator("items", ItemsSyntaxValidator.class);
        registerValidator("maximum", MaximumSyntaxValidator.class);
        registerValidator("maxItems", MaxItemsSyntaxValidator.class);
        registerValidator("maxLength", MaxLengthSyntaxValidator.class);
        registerValidator("minimum", MinimumSyntaxValidator.class);
        registerValidator("minItems", MinItemsSyntaxValidator.class);
        registerValidator("minLength", MinLengthSyntaxValidator.class);
        registerValidator("pattern", PatternSyntaxValidator.class);
        registerValidator("patternProperties",
            PatternPropertiesSyntaxValidator.class);
        registerValidator("properties", PropertiesSyntaxValidator.class);
        registerValidator("required", RequiredSyntaxValidator.class);
        registerValidator("title", TitleSyntaxValidator.class);
        registerValidator("type", TypeSyntaxValidator.class);
        registerValidator("uniqueItems", UniqueItemsSyntaxValidator.class);
    }

    /**
     * <p>Get the syntax validator for a given context,
     * calling {@link ValidationContext#getSchemaNode()} to grab the schema
     * node to validate. As the summary mentions, an unknown keyword to this
     * factory will trigger a failure by returning an {@link
     * AlwaysFalseValidator}.</p>
     * <p>This is also the place where ill-formed schemas are captured (ie,
     * a null input or a node which is not an object to begin with).
     * </p>
     *
     * @param context the validation context
     * @return the matching validator
     */
    public Validator getValidator(final ValidationContext context)
    {
        final JsonNode schema = context.getSchemaNode();
        final ValidationReport report = context.createReport(" [schema]");

        if (schema == null) {
            report.addMessage("schema is null");
            return new AlwaysFalseValidator(report);
        }

        if (!schema.isObject()) {
            report.addMessage("not a valid schema (not an object)");
            return new AlwaysFalseValidator(report);
        }

        final Set<String> fields
            = CollectionUtils.toSet(schema.getFieldNames());

        final Set<String> keywords = new HashSet<String>(validators.keySet());

        if (!keywords.containsAll(fields)) {
            fields.removeAll(keywords);
            for (final String field: fields)
                report.addMessage("unknown keyword " + field);
            return new AlwaysFalseValidator(report);
        }

        fields.retainAll(keywords);

        if (fields.isEmpty())
            return new AlwaysTrueSyntaxValidator(context);

        final Collection<Validator> collection
            = getValidators(context, fields);

        return collection.size() == 1 ? collection.iterator().next()
            : new MatchAllValidator(context, collection);
    }

    /**
     * Register a validator for a given keyword
     *
     * @param keyword the keyword
     * @param c the {@link SyntaxValidator} as a {@link Class} object
     */
    private void registerValidator(final String keyword,
        final Class<? extends SyntaxValidator> c)
    {
        validators.put(keyword, c);
    }

    /**
     * Return a list of validators for a schema node
     *
     * @param context the context
     * @param fields the list of keywords
     * @return the list of validators
     */
    private Collection<Validator> getValidators(
        final ValidationContext context, final Set<String> fields)
    {
        final Set<Validator> ret = new HashSet<Validator>();

        Class<? extends SyntaxValidator> c;
        Validator v;

        for (final String field: fields) {
            c = validators.get(field);
            try {
                v = buildValidator(c, context);
                ret.add(v);
            } catch (Exception e) {
                final ValidationReport report
                    = context.createReport(" [schema]");
                report.addMessage(String.format("cannot instantiate syntax "
                    + "validator for %s: %s: %s", field,
                    e.getClass().getName(), e.getMessage()));
                v = new AlwaysFalseValidator(report);
                return Arrays.asList(v);
            }
        }

        return ret;
    }

    /**
     * Instantiate a {@link SyntaxValidator}
     *
     * @param c the class
     * @param context the context
     * @return the instantiated validator
     * @throws NoSuchMethodException constructor not found
     * @throws InvocationTargetException see {@link InvocationTargetException}
     * @throws IllegalAccessException see {@link IllegalAccessException}
     * @throws InstantiationException see {@link InstantiationException}
     */
    private static Validator buildValidator(
        final Class<? extends Validator> c,
        final ValidationContext context)
        throws NoSuchMethodException, InvocationTargetException,
        IllegalAccessException, InstantiationException
    {
        final Constructor<? extends Validator> constructor
            = c.getConstructor(ValidationContext.class);

        return constructor.newInstance(context);
    }
}
