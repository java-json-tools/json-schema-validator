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

package org.eel.kitchen.jsonschema.keyword.common;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.keyword.AbstractTypeKeywordValidator;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

import java.util.List;

/**
 * Keyword validator for the {@code type} keyword (section 5.1)
 *
 * @see AbstractTypeKeywordValidator
 */
public final class TypeKeywordValidator
    extends AbstractTypeKeywordValidator
{
    private static final TypeKeywordValidator instance
        = new TypeKeywordValidator();

    private TypeKeywordValidator()
    {
        super("type");
    }

    public static TypeKeywordValidator getInstance()
    {
        return instance;
    }

    @Override
    protected ValidationReport doValidate(final ValidationContext context,
        final JsonNode instance, final TypeSet typeSet,
        final List<JsonNode> schemas)
    {
        final ValidationReport r1 = context.createReport();

        if (typeSet.matches(instance))
            return r1;

        r1.message(
            "instance type is not allowed (allowed types are: " + typeSet + ")");

        if (schemas.isEmpty())
            return r1;

        final ValidationReport r2 = context.createReport();

        ValidationReport tmp;

        for (final JsonNode schema: schemas) {
            tmp = validateSchema(context, schema, instance);
            if (tmp.isSuccess())
                return tmp;
            r2.mergeWith(tmp);
        }

        r1.mergeWith(r2);
        return r1;
    }
}
