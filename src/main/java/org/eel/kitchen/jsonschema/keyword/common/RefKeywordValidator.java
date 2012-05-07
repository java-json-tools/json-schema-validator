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
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonRefException;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.ValidationConfig;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.uri.URIHandler;
import org.eel.kitchen.util.JsonPointer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Keyword validator for {@code $ref} (draft version 5.28)
 *
 * <p>Please note that while you can register any URI scheme you want (see
 * {@link ValidationConfig#registerURIHandler(String, URIHandler)}),
 * relative URIs which are <b>not</b> JSON Pointers are not supported by
 * choice: we cannot tell where to base the lookup from (for JSON Pointers,
 * it's easy enough: it's the current schema).
 * </p>
 *
 * <p>Note that ref loop detection is not done here, nor is malformed JSON
 * Pointers. This is the role of {@link
 * ValidationContext#getValidator(JsonPointer, JsonNode, boolean)}.</p>
 *
 * @see URIHandler
 * @see JsonPointer
 */
public final class RefKeywordValidator
    extends KeywordValidator
{
    private static final RefKeywordValidator instance
        = new RefKeywordValidator();

    private RefKeywordValidator()
    {
        super("$ref");
    }

    public static RefKeywordValidator getInstance()
    {
        return instance;
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
     * </ul>
     *
     * @return the report from the spawned validator
     */
    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final String ref = context.getSchema().get(keyword).textValue();

        final URI uri, baseURI;
        final JsonPointer pointer;

        try {
            uri = new URI(ref);
            baseURI = new URI(uri.getScheme(), uri.getSchemeSpecificPart(),
                null);
            pointer = new JsonPointer(uri.getFragment());
        } catch (URISyntaxException e) {
            throw new JsonRefException("PROBLEM: invalid URI found (" + ref
                + "), syntax validation should have caught that", e);
        } catch (JsonSchemaException e) {
            throw new JsonRefException("Invalid JSON Pointer", e);
        }

        final ValidationContext ctx;
        try {
            ctx = context.fromURI(baseURI);
        } catch (IOException e) {
            throw new JsonRefException("cannot download schema at " + ref, e);
        } catch (IllegalArgumentException e) {
            throw new JsonRefException("cannot use ref " + ref, e);
        }

        return ctx.getValidator(pointer, instance, true)
            .validate(ctx, instance);
    }
}
