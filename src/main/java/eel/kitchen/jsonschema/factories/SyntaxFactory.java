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

public final class SyntaxFactory
{
    private final Map<String, Class<? extends SyntaxValidator>> validators
        = new HashMap<String, Class<? extends SyntaxValidator>>();

    public SyntaxFactory()
    {
        validators.put("additionalItems", AdditionalItemsValidator.class);
        validators.put("additionalProperties",
            AdditionalPropertiesValidator.class);
        validators.put("default", AlwaysTrueValidator.class);
        validators.put("dependencies", DependenciesValidator.class);
        validators.put("description", DescriptionValidator.class);
        validators.put("disallow", DisallowValidator.class);
        validators.put("divisibleBy", DivisibleByValidator.class);
        validators.put("$ref", DollarRefValidator.class);
        validators.put("$schema", DollarSchemaValidator.class);
        validators.put("enum", EnumValidator.class);
        validators.put("exclusiveMaximum", ExclusiveMaximumValidator.class);
        validators.put("exclusiveMinimum", ExclusiveMinimumValidator.class);
        validators.put("extends", ExtendsValidator.class);
        validators.put("format", FormatValidator.class);
        validators.put("id", IdValidator.class);
        validators.put("items", ItemsValidator.class);
        validators.put("maximum", MaximumValidator.class);
        validators.put("maxItems", MaxItemsValidator.class);
        validators.put("maxLength", MaxLengthValidator.class);
        validators.put("minimum", MinimumValidator.class);
        validators.put("minItems", MinItemsValidator.class);
        validators.put("minLength", MinLengthValidator.class);
        validators.put("pattern", PatternValidator.class);
        validators.put("patternProperties", PatternPropertiesValidator.class);
        validators.put("properties", PropertiesValidator.class);
        validators.put("required", RequiredValidator.class);
        validators.put("title", TitleValidator.class);
        validators.put("type", TypeValidator.class);
        validators.put("uniqueItems", UniqueItemsValidator.class);
    }

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

        final Collection<SyntaxValidator> collection
            = getValidators(context, fields);

        return collection.size() == 1 ? collection.iterator().next()
            : new MatchAllValidator(context, collection);
    }

    private Collection<SyntaxValidator> getValidators(
        final ValidationContext context, final Set<String> fields)
    {
        final Set<SyntaxValidator> ret = new HashSet<SyntaxValidator>();

        Class<? extends SyntaxValidator> c;
        SyntaxValidator v;

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

    private static SyntaxValidator buildValidator(
        final Class<? extends SyntaxValidator> c,
        final ValidationContext context)
        throws NoSuchMethodException, InvocationTargetException,
        IllegalAccessException, InstantiationException
    {
        final Constructor<? extends SyntaxValidator> constructor
            = c.getConstructor(ValidationContext.class);

        return constructor.newInstance(context);
    }
}
