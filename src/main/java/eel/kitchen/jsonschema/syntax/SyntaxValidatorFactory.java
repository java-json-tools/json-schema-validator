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

package eel.kitchen.jsonschema.syntax;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import eel.kitchen.jsonschema.base.MatchAllValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.context.ValidationContext;
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

public final class SyntaxValidatorFactory
{
    private final Map<String, Class<? extends Validator>> validators
        = new HashMap<String, Class<? extends Validator>>();

    public SyntaxValidatorFactory()
    {
        validators.put("additionalItems", AdditionalItemsValidator.class);
        validators.put("additionalProperties",
            AdditionalPropertiesValidator.class);
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

        final Collection<Validator> collection = getValidators(context, fields);

        return collection.size() == 1 ? collection.iterator().next()
            : new MatchAllValidator(context, collection);
    }

    private Collection<Validator> getValidators(final ValidationContext context,
        final Set<String> fields)
    {
        final Set<Validator> ret = new HashSet<Validator>();

        Class<? extends Validator> c;
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

    private static Validator buildValidator(final Class<? extends Validator> c,
        final ValidationContext context)
        throws NoSuchMethodException, InvocationTargetException,
        IllegalAccessException, InstantiationException
    {
        final Constructor<? extends Validator> constructor
            = c.getConstructor(ValidationContext.class);

        return constructor.newInstance(context);
    }
}
