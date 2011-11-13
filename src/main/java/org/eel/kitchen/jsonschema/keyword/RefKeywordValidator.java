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

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.util.JsonPointer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Keyword validator for {@code $ref} (draft version 5.28)
 *
 * <p>It only works "partially", in the sense that only HTTP URL refs and JSON
 * path refs are supported (or a combination of both,
 * as in {@code http://host.name/link/to/schema#/path/within/schema}). It is
 * unclear to the author how other types of URIs may be used,
 * and thus far he has seen no other examples.
 * </p>
 *
 * <p>Note that ref loop detection is not done here, nor is malformed JSON
 * paths. This is the role of {@link ValidationContext#getValidator(JsonPointer,
 * JsonNode)}.</p>
 */
public final class RefKeywordValidator
    extends KeywordValidator
{
    public RefKeywordValidator()
    {
        super("$ref");
    }

    /**
     * Validate the instance
     *
     * <p>Unlike all other validators:</p>
     * <ul>
     *     <li>this is the only one which will, if required,
     *     go over the net to grab new schemas;</li>
     *     <li>this is the only one which can spawn a {@link
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
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();

        final String ref = context.getSchemaNode().get("$ref").getTextValue();

        final URI uri, baseURI;
        final JsonPointer pointer;

        try {
            uri = new URI(ref);
            baseURI = new URI(uri.getScheme(), uri.getSchemeSpecificPart(),
                null);
            pointer = new JsonPointer(uri.getRawFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException("PROBLEM: invalid URI found ("
                + ref + "), syntax validation should have caught that", e);
        }

        final ValidationContext ctx;
        try {
            ctx = context.createContextFromURI(baseURI);
        } catch (IOException e) {
            report.error(String.format("cannot download schema at ref %s: %s: "
                + "%s", ref, e.getClass().getName(), e.getMessage()));
            return report;
        } catch (IllegalArgumentException e) {
            report.error(String.format("cannot use ref %s: %s", ref,
                e.getMessage()));
            return report;
        }

        return ctx.getValidator(pointer, instance).validate(ctx, instance);
    }
}
