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
import org.eel.kitchen.util.JsonLoader;

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
 * paths. This is the role of {@link ValidationContext#getValidator(String,
 * JsonNode)}.</p>
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
    public ValidationReport validate()
    {
        final JsonNode schemaNode = context.getSchemaNode();
        final String ref = schemaNode.get("$ref").getTextValue();

        final URI uri, baseURI;

        try {
            uri = new URI(ref);
            baseURI = new URI(uri.getScheme(), uri.getSchemeSpecificPart(),
                null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("PROBLEM: invalid URI found ("
                + ref + "), syntax validation should have caught that", e);
        }

        final boolean absoluteURI = uri.isAbsolute();

        if (!absoluteURI) {
            if (!uri.getSchemeSpecificPart().isEmpty()) {
                report.error("invalid URI " + ref + ": non absolute URI "
                    + "but non empty scheme specific part");
                return report;
            }
        } else if (!"http".equals(uri.getScheme())) {
            report.error("cannot use ref " + ref + ", only HTTP is "
                + "supported currently");
            return report;
        }

        String path = uri.getFragment();

        if (path == null)
            path = "";

        ValidationContext ctx = context;

        if (absoluteURI) {
            JsonNode newSchema;
            try {
                newSchema = context.fromCache(baseURI);
                if (newSchema == null)
                    newSchema = JsonLoader.fromURL(baseURI.toURL());
            } catch (IOException e) {
                report.error("cannot download schema: " + e.getMessage());
                return report;
            }
            ctx = context.newContext(baseURI, newSchema);
        }

        return ctx.getValidator(path, instance).validate();
    }
}
