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
import org.eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import org.eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import org.eel.kitchen.jsonschema.base.MatchAllValidator;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.AdditionalItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.AdditionalPropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DependenciesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DescriptionSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DisallowSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DivisibleBySyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DollarRefSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DollarSchemaSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.EnumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.ExclusiveMaximumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.ExclusiveMinimumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.ExtendsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.FormatSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.IdSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.ItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MaxItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MaxLengthSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MaximumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MinItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MinLengthSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MinimumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.PatternPropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.PatternSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.PropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.RequiredSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.TitleSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.TypeSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.UniqueItemsSyntaxValidator;
import org.eel.kitchen.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Factory providing syntax checking validators for a schema
 *
 * <p>Syntax validators are used to validate the schema itself. In doing so,
 * they ensure that keyword validators always have correct data to deal with.
 * </p>
 *
 * <p>Normally, you should never have to instantiate this factory yourself
 * (in fact, the only current user for this is {@link ValidationContext}).</p>
 *
 * <p>Note that unknown keywords to this factory trigger a validation
 * <b>failure</b>. Therefore, it is important that all keywords be
 * registered. This is on purpose.</p>
 */
public final class SyntaxFactory
{
    /**
     * Map pairing a schema keyword with its corresponding syntax validator
     */
    private final Map<String, SyntaxValidator> validators
        = new HashMap<String, SyntaxValidator>();

    /**
     * The set of ignored keywords for this factory
     *
     * <p>Note that an ignored keyword is <b>not</b> the same as an unknown
     * keyword, it simply means that if the keyword is found in the schema,
     * it is assumed to always be valid.</p>
     */
    private final Set<String> ignoredKeywords = new HashSet<String>();

    /**
     * Constructor, registering all validators with {@link
     * #registerValidator(String, Class)}
     */
    public SyntaxFactory()
    {
        register("additionalItems", new AdditionalItemsSyntaxValidator());
        register("additionalProperties",
            new AdditionalPropertiesSyntaxValidator());
        register("dependencies", new DependenciesSyntaxValidator());
        register("description", new DescriptionSyntaxValidator());
        register("disallow", new DisallowSyntaxValidator());
        register("divisibleBy", new DivisibleBySyntaxValidator());
        register("$ref", new DollarRefSyntaxValidator());
        register("$schema", new DollarSchemaSyntaxValidator());
        register("enum", new EnumSyntaxValidator());
        register("exclusiveMaximum", new ExclusiveMaximumSyntaxValidator());
        register("exclusiveMinimum", new ExclusiveMinimumSyntaxValidator());
        register("extends", new ExtendsSyntaxValidator());
        register("format", new FormatSyntaxValidator());
        register("id", new IdSyntaxValidator());
        register("items", new ItemsSyntaxValidator());
        register("maximum", new MaximumSyntaxValidator());
        register("maxItems", new MaxItemsSyntaxValidator());
        register("maxLength", new MaxLengthSyntaxValidator());
        register("minimum", new MinimumSyntaxValidator());
        register("minItems", new MinItemsSyntaxValidator());
        register("minLength", new MinLengthSyntaxValidator());
        register("pattern", new PatternSyntaxValidator());
        register("patternProperties", new PatternPropertiesSyntaxValidator());
        register("properties", new PropertiesSyntaxValidator());
        register("required", new RequiredSyntaxValidator());
        register("title", new TitleSyntaxValidator());
        register("type", new TypeSyntaxValidator());
        register("uniqueItems", new UniqueItemsSyntaxValidator());

        ignoredKeywords.add("default");
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

        fields.removeAll(ignoredKeywords);

        final Set<String> keywords = new HashSet<String>(validators.keySet());

        if (!keywords.containsAll(fields)) {
            fields.removeAll(keywords);
            for (final String field: fields)
                report.addMessage("unknown keyword " + field);
            return new AlwaysFalseValidator(report);
        }

        fields.retainAll(keywords);

        if (fields.isEmpty())
            return new AlwaysTrueValidator();

        final Collection<Validator> collection = getValidators(fields);

        return collection.size() == 1 ? collection.iterator().next()
            : new MatchAllValidator(collection);
    }

    /**
     * Register a validator for a given keyword
     *
     * <p>Note that if the class argument is {@code null},
     * the keyword will be registered but validation will be happily ignored.
     * If you choose to go this route, be sure that the matching
     * {@link KeywordValidator} can handle the situation!</p>
     *
     * @param keyword the keyword
     * @param c the {@link SyntaxValidator} as a {@link Class} object
     * @throws IllegalArgumentException if a validator has already been
     * registered for this keyword
     *
     * @see #unregisterValidator(String)
     */
    public void registerValidator(final String keyword,
        final Class<? extends SyntaxValidator> c)
    {
        if (ignoredKeywords.contains(keyword) || validators.containsKey(keyword))
            throw new IllegalArgumentException("keyword already registered");

        if (c == null) {
            ignoredKeywords.add(keyword);
            return;
        }

        final SyntaxValidator sv;

        Exception exception;
        try {
            sv = buildValidator(c);
            validators.put(keyword, sv);
            return;
        } catch (NoSuchMethodException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InstantiationException e) {
            exception = e;
        }

        final String errmsg = String.format("cannot instantiate validator: "
            + "%s: %s", exception.getClass().getName(), exception.getMessage());
        throw new IllegalArgumentException(errmsg);

    }

    /**
     * Private registration method
     *
     * @param keyword the keyword
     * @param sv the validator, already instantiated
     */
    private void register(final String keyword, final SyntaxValidator sv)
    {
        validators.put(keyword, sv);
    }

    /**
     * Unregister a validator for the given keyword
     *
     * <p>This method <b>must</b> be called before registering a new keyword.
     * Unlike the latter however, it silently ignores unexisting keywords.</p>
     *
     * @param keyword the victim
     *
     * @see #registerValidator(String, Class)
     */
    public void unregisterValidator(final String keyword)
    {
        /*
         * Unlike for registering, we choose to blindly ignore unregistering
         * of non existing keywords.
         */
        validators.remove(keyword);
        ignoredKeywords.remove(keyword);
    }

    /**
     * Return a list of validators for a schema node
     *
     * @param fields the list of keywords
     * @return the list of validators
     */
    private Collection<Validator> getValidators(
        final Set<String> fields)
    {
        final Set<Validator> ret = new HashSet<Validator>();

        for (final String field: fields)
            ret.add(validators.get(field));

        return ret;
    }

    /**
     * Instantiate a {@link SyntaxValidator}
     *
     * @param c the class
     * @return the instantiated validator
     * @throws NoSuchMethodException constructor not found
     * @throws InvocationTargetException see {@link InvocationTargetException}
     * @throws IllegalAccessException see {@link IllegalAccessException}
     * @throws InstantiationException see {@link InstantiationException}
     */
    private static SyntaxValidator buildValidator(
        final Class<? extends SyntaxValidator> c)
        throws NoSuchMethodException, InvocationTargetException,
        IllegalAccessException, InstantiationException
    {
        final Constructor<? extends SyntaxValidator> constructor
            = c.getConstructor();

        return constructor.newInstance();
    }
}
