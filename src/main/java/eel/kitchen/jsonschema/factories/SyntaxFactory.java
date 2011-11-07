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
import eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import eel.kitchen.jsonschema.base.MatchAllValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.jsonschema.syntax.AdditionalItemsValidator;
import eel.kitchen.jsonschema.syntax.AdditionalPropertiesValidator;
import eel.kitchen.jsonschema.syntax.AlwaysTrueSyntaxValidator;
import eel.kitchen.jsonschema.syntax.DependenciesValidator;
import eel.kitchen.jsonschema.syntax.DescriptionValidator;
import eel.kitchen.jsonschema.syntax.DisallowValidator;
import eel.kitchen.jsonschema.syntax.DivisibleByValidator;
import eel.kitchen.jsonschema.syntax.DollarRefValidator;
import eel.kitchen.jsonschema.syntax.DollarSchemaValidator;
import eel.kitchen.jsonschema.syntax.EnumValidator;
import eel.kitchen.jsonschema.syntax.ExclusiveMaximumValidator;
import eel.kitchen.jsonschema.syntax.ExclusiveMinimumValidator;
import eel.kitchen.jsonschema.syntax.ExtendsValidator;
import eel.kitchen.jsonschema.syntax.FormatValidator;
import eel.kitchen.jsonschema.syntax.IdValidator;
import eel.kitchen.jsonschema.syntax.ItemsValidator;
import eel.kitchen.jsonschema.syntax.MaxItemsValidator;
import eel.kitchen.jsonschema.syntax.MaxLengthValidator;
import eel.kitchen.jsonschema.syntax.MaximumValidator;
import eel.kitchen.jsonschema.syntax.MinItemsValidator;
import eel.kitchen.jsonschema.syntax.MinLengthValidator;
import eel.kitchen.jsonschema.syntax.MinimumValidator;
import eel.kitchen.jsonschema.syntax.PatternPropertiesValidator;
import eel.kitchen.jsonschema.syntax.PatternValidator;
import eel.kitchen.jsonschema.syntax.PropertiesValidator;
import eel.kitchen.jsonschema.syntax.RequiredValidator;
import eel.kitchen.jsonschema.syntax.SyntaxValidator;
import eel.kitchen.jsonschema.syntax.TitleValidator;
import eel.kitchen.jsonschema.syntax.TypeValidator;
import eel.kitchen.jsonschema.syntax.UniqueItemsValidator;
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
        registerValidator("additionalItems", AdditionalItemsValidator.class);
        registerValidator("additionalProperties",
            AdditionalPropertiesValidator.class);
        registerValidator("default", AlwaysTrueSyntaxValidator.class);
        registerValidator("dependencies", DependenciesValidator.class);
        registerValidator("description", DescriptionValidator.class);
        registerValidator("disallow", DisallowValidator.class);
        registerValidator("divisibleBy", DivisibleByValidator.class);
        registerValidator("$ref", DollarRefValidator.class);
        registerValidator("$schema", DollarSchemaValidator.class);
        registerValidator("enum", EnumValidator.class);
        registerValidator("exclusiveMaximum", ExclusiveMaximumValidator.class);
        registerValidator("exclusiveMinimum", ExclusiveMinimumValidator.class);
        registerValidator("extends", ExtendsValidator.class);
        registerValidator("format", FormatValidator.class);
        registerValidator("id", IdValidator.class);
        registerValidator("items", ItemsValidator.class);
        registerValidator("maximum", MaximumValidator.class);
        registerValidator("maxItems", MaxItemsValidator.class);
        registerValidator("maxLength", MaxLengthValidator.class);
        registerValidator("minimum", MinimumValidator.class);
        registerValidator("minItems", MinItemsValidator.class);
        registerValidator("minLength", MinLengthValidator.class);
        registerValidator("pattern", PatternValidator.class);
        registerValidator("patternProperties", PatternPropertiesValidator.class);
        registerValidator("properties", PropertiesValidator.class);
        registerValidator("required", RequiredValidator.class);
        registerValidator("title", TitleValidator.class);
        registerValidator("type", TypeValidator.class);
        registerValidator("uniqueItems", UniqueItemsValidator.class);
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
            return new AlwaysTrueValidator(context);

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
