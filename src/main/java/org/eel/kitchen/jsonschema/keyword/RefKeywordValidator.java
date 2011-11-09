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

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.context.ValidationContext;

import java.io.IOException;
import java.net.URI;

/**
 * <p>Keyword validator for {@code $ref} (draft version 5.28)</p>
 *
 * <p>It only works "partially", in the sense that only URL refs and JSON
 * path refs are supported (or a combination of both,
 * as in {@code http://host.name/link/to/schema#/path/within/schema}). It is
 * unclear to the author how other types of URIs may be used,
 * and thus far he has seen no other examples.
 * </p>
 *
 * <p>It works as the spec says: it resolves the ref (using {@link
 * ValidationContext#resolveRef(String)}</p> and spawns a Validator for the
 * returned schema -- IF it is valid.</p>
 *
 * <p>Note that this validator does <b>not</b> detect loops: it relies on
 * {@link ValidationContext#resolveRef(String)} to do this for it.</p>
 */
public final class RefKeywordValidator
    extends KeywordValidator
{
    public RefKeywordValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);
    }

    /**
     * <p>Validate the instance. This calls {@link
     * ValidationContext#resolveRef(String)} on the current {@link #context}
     * to obtain a new context from which it obtains a new {@link org.eel.kitchen.jsonschema.base.Validator},
     * and returns the result of this validator's {@link org.eel.kitchen.jsonschema.base.Validator#validate()}
     * method. Unlike all other validators:</p>
     * <ul>
     *     <li>this is the only one which can return a {@link
     *     ValidationContext} with a different root schema (if the ref
     *     represents an absolute {@link URI});</li>
     *     <li>this is the only validator implementation which can spawn
     *     errors (ie, {@link ValidationReport#isError()} returns {@code true})
     *     and not only failures.</li>
     * </ul>
     *
     * @return the report from the spawned validator
     */
    @Override
    public ValidationReport validate()
    {
        final JsonNode schemaNode = context.getSchemaNode();
        final String ref = schemaNode.get("$ref").getTextValue();

        final ValidationContext ctx;

        try {
            ctx = context.resolveRef(ref);
        } catch (IOException e) {
            report.error(String.format("cannot resolve ref %s: %s: %s",
                ref, e.getClass().getName(), e.getMessage()));
            return report;
        }

        return ctx.getValidator(instance).validate();
    }
}
