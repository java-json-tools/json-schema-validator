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

package eel.kitchen.jsonschema.v2.schema;

import eel.kitchen.jsonschema.v2.instance.Instance;
import eel.kitchen.jsonschema.v2.syntax.SchemaChecker;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static eel.kitchen.jsonschema.v2.schema.ValidationMode.*;

public final class SchemaFactory
{
    private static final SchemaChecker checker = SchemaChecker.getInstance();

    private static final EnumSet<ValidationMode> SET_MODES
        = EnumSet.of(VALIDATE_ALL, VALIDATE_ANY);

    private static final EnumSet<ValidationMode> BOOLEAN_MODES
        = EnumSet.of(VALIDATE_MATCHFAIL, VALIDATE_NORMAL);

    private final JsonNode rootSchema;

    public SchemaFactory(final JsonNode rootSchema)
    {
        this.rootSchema = rootSchema;
    }

    public Schema buildSchema(final EnumSet<ValidationMode> mode,
        final Set<JsonNode> set)
    {
        if (!isValid(mode))
            return failure(Arrays.asList("illegal schema build flags"));

        final Set<Schema> schemaSet = new LinkedHashSet<Schema>();

        for (final JsonNode node: set)
            schemaSet.add(buildSingleSchema(VALIDATE_NORMAL, node));

        return buildSchemaFromSet(mode, schemaSet);
    }

    public Schema buildSchemaFromSet(final EnumSet<ValidationMode> mode,
        final Set<Schema> set)
    {
        if (!isValid(mode))
            return failure(Arrays.asList("illegal schema build flags"));

        final ValidationMode setMode = toSetMode(mode),
            booleanMode = toBooleanMode(mode);

        Schema ret;

        if (set.size() == 1) {
            ret = set.iterator().next();
            return booleanMode == VALIDATE_NORMAL ? ret
                : new NegativeMatchSchema(ret);
        }

        switch (setMode) {
            case VALIDATE_ALL:
                ret = new MatchAllSchema(set);
                break;
            case VALIDATE_ANY:
                ret = new MatchAnySchema(set);
                break;
            default:
                throw new RuntimeException("How did I even get there???");
        }

        if (booleanMode == VALIDATE_MATCHFAIL)
            ret = new NegativeMatchSchema(ret);

        return ret;
    }

    public Schema buildSingleSchema(final ValidationMode mode,
        final JsonNode node)
    {
        final List<String> list = checker.check(this, node);

        if (!list.isEmpty())
            return failure(list);

        Schema ret = new SingleSchema(this, node);

        if (mode == VALIDATE_MATCHFAIL)
            ret = new NegativeMatchSchema(ret);

        return ret;
    }

    private static boolean isValid(final EnumSet<ValidationMode> mode)
    {
        if (mode.containsAll(SET_MODES))
            return false;

        if (mode.containsAll(BOOLEAN_MODES))
            return false;

        return true;
    }

    private static ValidationMode toSetMode(final EnumSet<ValidationMode> mode)
    {
        return mode.contains(VALIDATE_ALL) ? VALIDATE_ALL : VALIDATE_ANY;
    }

    private static ValidationMode toBooleanMode(
        final EnumSet<ValidationMode> mode)
    {
        return mode.contains(VALIDATE_MATCHFAIL) ? VALIDATE_MATCHFAIL
            :  VALIDATE_NORMAL;
    }

    private static Schema failure(final List<String> messages)
    {
        return new Schema()
        {
            @Override
            public Schema getSchema(final String path)
            {
                return null;
            }

            @Override
            public void validate(final ValidationState state,
                final Instance instance)
            {
                state.addMessages(messages);
            }
        };

    }
}
