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

package org.eel.kitchen.jsonschema.uri;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.eel.kitchen.jsonschema.keyword.NumericKeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.ref.SchemaRegistry;
import org.eel.kitchen.jsonschema.report.ValidationDomain;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.util.JsonLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to fetch JSON documents
 *
 * <p>This uses a map of {@link URIDownloader} instances to fetch the contents
 * of a URI as an {@link InputStream}, then tries and turns this content into
 * JSON using an {@link ObjectMapper}.</p>
 *
 * <p>Normally, you will never use this class directly.</p>
 *
 * @see SchemaRegistry
 * @see JsonLoader
 */
public class URIManager
{
    /**
     * Our object mapper
     *
     * <p>Note that it uses {@link
     * DeserializationFeature#USE_BIG_DECIMAL_FOR_FLOATS} to deserialize, for
     * accuracy reasons.</p>
     *
     * @see NumericKeywordValidator
     */
    private static final ObjectMapper mapper = new ObjectMapper()
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

    /**
     * Map of downloaders (schemes as keys, {@link URIDownloader} instances
     * as values)
     */
    private final Map<String, URIDownloader> downloaders
        = new HashMap<String, URIDownloader>();

    /**
     * Map for URI redirections
     *
     * <p>This map will be used if you wish for an absolute URI to be redirected
     * to another URI of your choice, which must also be absolute.</p>
     *
     * <p>Note that this map requires URIs to be <i>exact</i>. Currently, you
     * cannot, for example, redirect a whole namespace this way.</p>
     */
    private final Map<URI, URI> URIRedirections = Maps.newHashMap();

    public URIManager()
    {
        downloaders.put("http", DefaultURIDownloader.getInstance());
        downloaders.put("ftp", DefaultURIDownloader.getInstance());
        downloaders.put("file", DefaultURIDownloader.getInstance());
        downloaders.put("jar", DefaultURIDownloader.getInstance());
        downloaders.put("resource", ResourceURIDownloader.getInstance());
    }

    /**
     * Register a new downloader for a given URI scheme
     *
     * @param scheme the scheme
     * @param downloader the {@link URIDownloader} instance
     * @throws NullPointerException scheme is null
     * @throws IllegalArgumentException scheme is empty, or is already
     * registered
     */
    public void registerScheme(final String scheme,
        final URIDownloader downloader)
    {
        Preconditions.checkNotNull(scheme, "scheme is null");
        Preconditions.checkArgument(!scheme.isEmpty(), "scheme is empty");

        try {
            new URI(scheme, "x", "y");
        } catch (URISyntaxException ignored) {
            throw new IllegalArgumentException("illegal scheme \"" + scheme
                + '"');
        }

        downloaders.put(scheme, downloader);
    }

    /**
     * Unregister a downloader for a given scheme
     *
     * @param scheme the scheme
     * @throws NullPointerException scheme is null
     */
    public void unregisterScheme(final String scheme)
    {
        Preconditions.checkNotNull(scheme, "scheme is null");
        downloaders.remove(scheme);
    }

    /**
     * Add a URI rediction
     *
     * <p>The typical use case for this is if you have a local copy of a schema
     * whose id is normally unreachable. You can transform all references to
     * this schema's URI to another URI which is reachable by your application.
     * </p>
     *
     * <p>Note that the given strings will be considered as JSON References, and
     * that both arguments must be valid absolute JSON References. For the
     * recall, there is more to it than URIs being absolute: their fragment part
     * must also be empty or null.</p>
     *
     * @param from the original URI
     * @param to an URI which is reachable
     * @throws IllegalArgumentException either of {@code from} or {@code to} are
     * invalid URIs, or are not absolute JSON References
     */
    public void addRedirection(final String from, final String to)
    {
        final URI sourceURI, destURI;

        try {
            sourceURI = JsonRef.fromString(from).getRootAsURI();
        } catch (JsonSchemaException ignored) {
            throw new IllegalArgumentException("source URI " + from + " is not"
                + " an absolute JSON Reference");
        }

        try {
            destURI = JsonRef.fromString(to).getRootAsURI();
        } catch (JsonSchemaException ignored) {
            throw new IllegalArgumentException("destination URI " + to
                + " is not an absolute JSON Reference");
        }

        URIRedirections.put(sourceURI, destURI);
    }

    /**
     * Get the content at a given URI as a {@link JsonNode}
     *
     * @param uri the URI
     * @return the content
     * @throws JsonSchemaException scheme is not registered, failed to get
     * content, or content is not JSON
     */
    public JsonNode getContent(final URI uri)
        throws JsonSchemaException
    {
        Preconditions.checkNotNull(uri, "null URI");
        Preconditions.checkArgument(uri.isAbsolute(), "requested URI (" + uri
            + ") is not absolute");

        final URI target = URIRedirections.containsKey(uri)
            ? URIRedirections.get(uri) : uri;

        final String scheme = target.getScheme();

        final ValidationMessage.Builder msg
            = new ValidationMessage.Builder(ValidationDomain.REF_RESOLVING)
            .setKeyword("N/A").addInfo("uri", target); // FIXME...

        final URIDownloader downloader = downloaders.get(scheme);

        if (downloader == null) {
            msg.setMessage("cannot handle scheme").addInfo("scheme", scheme);
            throw new JsonSchemaException(msg.build());
        }

        final InputStream in;

        try {
            in = downloader.fetch(target);
        } catch (IOException e) {
            msg.setMessage("cannot fetch content from URI");
            throw new JsonSchemaException(msg.build(), e);
        }

        try {
            // Note: ObjectMapper's .readTree() closes the InputStream after it
            // is done with it!
            return mapper.readTree(in);
        } catch (IOException e) {
            msg.setMessage("content fetched from URI is not valid JSON");
            throw new JsonSchemaException(msg.build(), e);
        }
    }
}
