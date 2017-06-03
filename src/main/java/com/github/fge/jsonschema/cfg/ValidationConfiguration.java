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

import com.github.fge.Frozen;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.library.Keyword;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Validation configuration (frozen instance)
 *
 * <p>This allows you to configure the following aspects of validation:</p>
 *
 * <ul>
 *     <li>add your own schema keywords via libraries, with an associated {@code
 *     $schema} value;</li>
 *     <li>whether to use {@code format};</li>
 *     <li>what default keyword library should be used.</li>
 * </ul>
 *
 * <p>The default configuration has both draft v4 and draft v3 libraries
 * preloaded, and {@code format} validation is enabled; the default library to
 * use is draft v4.</p>
 *
 * @see ValidationConfigurationBuilder
 * @see Keyword
 * @see Library
 */
public final class ValidationConfiguration
    implements Frozen<ValidationConfigurationBuilder>
{
    /**
     * Map of keyword libraries and their associated URIs
     *
     * <p>The URIs here are what is expected in {@code $schema}.</p>
     */
    final Map<JsonRef, Library> libraries;

    /**
     * Default keyword library to use
     *
     * <p>This is the library used when no {@code $schema} could be found in
     * a submitted/downloaded schema.</p>
     */
    final Library defaultLibrary;

    /**
     * Whether to use {@code format} in the resulting factory
     */
    final boolean useFormat;
    
    /**
     * Cache size for processing validations
     */
    final int cacheSize;

    /**
     * The set of syntax messages
     */
    final MessageBundle syntaxMessages;

    /**
     * The set of validation messages
     */
    final MessageBundle validationMessages;

    /**
     * Return a new thawed instance of the default configuration
     *
     * @return a new configuration builder
     * @see ValidationConfigurationBuilder#ValidationConfigurationBuilder()
     */
    public static ValidationConfigurationBuilder newBuilder()
    {
        return new ValidationConfigurationBuilder();
    }

    /**
     * Return a default, frozen configuration
     *
     * @return a new configuration
     */
    public static ValidationConfiguration byDefault()
    {
        return newBuilder().freeze();
    }

    /**
     * Build a new frozen configuration out of a thawed one
     *
     * @param builder the source configuration
     * @see ValidationConfigurationBuilder#freeze()
     */
    ValidationConfiguration(final ValidationConfigurationBuilder builder)
    {
        libraries = ImmutableMap.copyOf(builder.libraries);
        defaultLibrary = builder.defaultLibrary;
        useFormat = builder.useFormat;
        cacheSize = builder.cacheSize;
        syntaxMessages = builder.syntaxMessages;
        validationMessages = builder.validationMessages;
    }

    /**
     * Return the map of libraries for this configuration
     *
     * @return an immutable map
     */
    public Map<JsonRef, Library> getLibraries()
    {
        return libraries;
    }

    /**
     * Return the default library to use
     *
     * @return a library
     */
    public Library getDefaultLibrary()
    {
        return defaultLibrary;
    }

    /**
     * Whether {@code format} should be used
     *
     * @return {@code true} if the answer is yes
     */
    public boolean getUseFormat()
    {
        return useFormat;
    }
    
    public int getCacheSize()
    {
    	return cacheSize;
    }

    public MessageBundle getSyntaxMessages()
    {
        return syntaxMessages;
    }

    public MessageBundle getValidationMessages()
    {
        return validationMessages;
    }

    /**
     * Return a thawed instance out of this frozen configuration
     *
     * @return a {@link ValidationConfigurationBuilder}
     * @see ValidationConfigurationBuilder#ValidationConfigurationBuilder(ValidationConfiguration)
     */
    @Override
    public ValidationConfigurationBuilder thaw()
    {
        return new ValidationConfigurationBuilder(this);
    }
}
