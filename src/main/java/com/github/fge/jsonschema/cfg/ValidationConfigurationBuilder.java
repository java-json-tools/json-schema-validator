/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.cfg;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;
import com.github.fge.jsonschema.library.DraftV3Library;
import com.github.fge.jsonschema.library.DraftV4Library;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.load.configuration.RefSanityChecks;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.Thawed;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.github.fge.jsonschema.messages.ConfigurationMessages.*;

/**
 * Validation configuration (mutable instance)
 *
 * @see ValidationConfiguration
 */
public final class ValidationConfigurationBuilder
    implements Thawed<ValidationConfiguration>
{
    /**
     * Default libraries to use
     *
     * <p>Those are the libraries for draft v3 core and draft v4 core.</p>
     *
     * @see SchemaVersion
     * @see DraftV3Library
     * @see DraftV4Library
     */
    private static final Map<SchemaVersion, Library> DEFAULT_LIBRARIES;

    static {
        DEFAULT_LIBRARIES = Maps.newEnumMap(SchemaVersion.class);
        DEFAULT_LIBRARIES.put(SchemaVersion.DRAFTV3, DraftV3Library.get());
        DEFAULT_LIBRARIES.put(SchemaVersion.DRAFTV4, DraftV4Library.get());
    }

    /**
     * The set of libraries to use
     */
    final Map<JsonRef, Library> libraries;

    /**
     * The default library to use (draft v4 by default)
     */
    Library defaultLibrary = DEFAULT_LIBRARIES.get(SchemaVersion.DRAFTV4);

    /**
     * Whether to use {@code format} ({@code true} by default)
     */
    boolean useFormat = true;

    ValidationConfigurationBuilder()
    {
        libraries = Maps.newHashMap();
        JsonRef ref;
        Library library;
        for (final Map.Entry<SchemaVersion, Library> entry:
            DEFAULT_LIBRARIES.entrySet()) {
            ref = JsonRef.fromURI(entry.getKey().getLocation());
            library = entry.getValue();
            libraries.put(ref, library);
        }
    }

    /**
     * Constructor from a frozen instance
     *
     * @param cfg the frozen configuration
     * @see ValidationConfiguration#thaw()
     */
    ValidationConfigurationBuilder(final ValidationConfiguration cfg)
    {
        libraries = Maps.newHashMap(cfg.libraries);
        defaultLibrary = cfg.defaultLibrary;
        useFormat = cfg.useFormat;
    }

    /**
     * Add a {@code $schema} and matching library to this configuration
     *
     * @param uri the value for {@code $schema}
     * @param library the library
     * @return this
     * @throws ValidationConfigurationError provided URI is null, not a URI, or
     * not an absolute JSON Reference; library is null; or there already exists
     * a library for this value of {@code $schema}
     */
    public ValidationConfigurationBuilder addLibrary(final String uri,
        final Library library)
    {
        final JsonRef ref = RefSanityChecks.absoluteRef(uri);
        if (library == null)
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(NULL_LIBRARY));
        if (libraries.containsKey(ref))
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(DUP_LIBRARY).put("uri", ref));
        libraries.put(ref, library);
        return this;
    }

    /**
     * Set the default schema version for this configuration
     *
     * <p>This will set the default library to use to the one registered for
     * this schema version.</p>
     *
     * @param version the version
     * @return this
     * @throws ValidationConfigurationError version is null
     */
    public ValidationConfigurationBuilder setDefaultVersion(
        final SchemaVersion version)
    {
        if (version == null)
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(NULL_VERSION));
        /*
         * They are always in, so this is safe
         */
        defaultLibrary = DEFAULT_LIBRARIES.get(version);
        return this;
    }

    /**
     * Add a library and sets it as the default
     *
     * @param uri the value for {@code $schema}
     * @param library the library
     * @return this
     * @see #addLibrary(String, Library)
     */
    public ValidationConfigurationBuilder setDefaultLibrary(final String uri,
        final Library library)
    {
        addLibrary(uri, library);
        defaultLibrary = library;
        return this;
    }

    /**
     * Tell whether the resulting configuration has support for {@code format}
     *
     * @param useFormat {@code true} if it must be used
     * @return this
     */
    public ValidationConfigurationBuilder setUseFormat(final boolean useFormat)
    {
        this.useFormat = useFormat;
        return this;
    }

    /**
     * Return a frozen version of this configuration
     *
     * @return a {@link ValidationConfiguration}
     * @see ValidationConfiguration#ValidationConfiguration(ValidationConfigurationBuilder)
     */
    @Override
    public ValidationConfiguration freeze()
    {
        return new ValidationConfiguration(this);
    }
}
