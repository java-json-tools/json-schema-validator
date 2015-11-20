/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.cfg;

import com.github.fge.Thawed;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.core.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.core.messages.JsonSchemaSyntaxMessageBundle;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.library.DraftV3Library;
import com.github.fge.jsonschema.library.DraftV4HyperSchemaLibrary;
import com.github.fge.jsonschema.library.DraftV4Library;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.jsonschema.messages.JsonSchemaValidationBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Validation configuration (mutable instance)
 *
 * @see ValidationConfiguration
 */
public final class ValidationConfigurationBuilder
    implements Thawed<ValidationConfiguration>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaConfigurationBundle.class);

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
        DEFAULT_LIBRARIES.put(SchemaVersion.DRAFTV4_HYPERSCHEMA,
            DraftV4HyperSchemaLibrary.get());
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
    
    /**
     * Cache maximum size of 512 records by default
     */
    int cacheSize = 512;

    /**
     * The set of syntax messages
     */
    MessageBundle syntaxMessages;

    /**
     * The set of validation messages
     */
    MessageBundle validationMessages;

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
        syntaxMessages = MessageBundles
            .getBundle(JsonSchemaSyntaxMessageBundle.class);
        validationMessages = MessageBundles
            .getBundle(JsonSchemaValidationBundle.class);
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
        cacheSize = cfg.cacheSize;
        syntaxMessages = cfg.syntaxMessages;
        validationMessages = cfg.validationMessages;
    }

    /**
     * Add a {@code $schema} and matching library to this configuration
     *
     * @param uri the value for {@code $schema}
     * @param library the library
     * @return this
     * @throws NullPointerException URI us null or library is null
     * @throws IllegalArgumentException string is not a URI, or not an absolute
     * JSON Reference; or a library already exists at this URI.
     */
    public ValidationConfigurationBuilder addLibrary(final String uri,
        final Library library)
    {
        final JsonRef ref;

        try {
            ref = JsonRef.fromString(uri);
        } catch (JsonReferenceException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        BUNDLE.checkArgumentPrintf(ref.isAbsolute(),
            "refProcessing.uriNotAbsolute", ref);
        BUNDLE.checkNotNull(library, "nullLibrary");
        BUNDLE.checkArgumentPrintf(libraries.put(ref, library) == null,
            "dupLibrary", ref);
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
     * @throws NullPointerException version is null
     */
    public ValidationConfigurationBuilder setDefaultVersion(
        final SchemaVersion version)
    {
        BUNDLE.checkNotNull(version, "nullVersion");
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

    public ValidationConfigurationBuilder setSyntaxMessages(
        final MessageBundle syntaxMessages)
    {
        BUNDLE.checkNotNull(syntaxMessages, "nullMessageBundle");
        this.syntaxMessages = syntaxMessages;
        return this;
    }

    public ValidationConfigurationBuilder setValidationMessages(
        final MessageBundle validationMessages)
    {
        BUNDLE.checkNotNull(validationMessages, "nullMessageBundle");
        this.validationMessages = validationMessages;
        return this;
    }

    public ValidationConfigurationBuilder setCacheSize(
            final int cacheSize)
    {
        BUNDLE.checkArgument(cacheSize >= -1, "invalidCacheSize");
        this.cacheSize = cacheSize;
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
