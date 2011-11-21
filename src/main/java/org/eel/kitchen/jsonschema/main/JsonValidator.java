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

package org.eel.kitchen.jsonschema.main;


import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.factories.ValidatorFactory;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.uri.URIHandler;
import org.eel.kitchen.jsonschema.uri.URIHandlerFactory;
import org.eel.kitchen.util.JsonLoader;
import org.eel.kitchen.util.JsonPointer;
import org.eel.kitchen.util.NodeType;
import org.eel.kitchen.util.SchemaVersion;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The main interface to use for JSON Schema validation
 *
 * <p>All accesses to this class are protected by {@link #ctxlock}, which is a
 * {@link ReentrantReadWriteLock}. Namely, all modifications
 * (registering/unregistering a validator, setting a feature etc) need to
 * acquire the write lock, whereas instance validation needs the read lock.</p>
 *
 * @see JsonLoader
 * @see ValidationContext
 */
public final class JsonValidator
{
    private SchemaVersion defaultVersion = SchemaVersion.DRAFT_V3;

    /**
     * Lock to protect context creation
     */
    private final ReentrantReadWriteLock ctxlock
        = new ReentrantReadWriteLock();

    private final Map<SchemaVersion, ValidatorFactory> factories
        = new EnumMap<SchemaVersion, ValidatorFactory>(SchemaVersion.class);

    /**
     * The schema provider
     */
    private final SchemaProvider provider;

    /**
     * Set of features enabled for this validator (see {@link
     * ValidationFeature})
     */
    private final EnumSet<ValidationFeature> features
        = EnumSet.noneOf(ValidationFeature.class);

    /**
     * Report generator
     */
    private ReportFactory reports;

    /**
     * This validator's {@link ValidationContext}
     */
    private ValidationContext context;

    /**
     * The constructor
     *
     * @param schema the root schema to use for validation
     * @throws JsonValidationFailureException the initial JSON node is not a
     * schema
     */
    public JsonValidator(final JsonNode schema)
        throws JsonValidationFailureException
    {
        provider = new SchemaProvider(defaultVersion, schema);
        reports = new ReportFactory(false);
        buildFactories(false);
        context = new ValidationContext(factories, provider, reports);
    }

    private void buildFactories(final boolean skipSyntax)
    {
        ValidatorBundle bundle;
        for (final SchemaVersion version: SchemaVersion.values()) {
            bundle = version.getBundle();
            factories.put(version, new ValidatorFactory(bundle, skipSyntax));
        }
    }

    /**
     * Returns the default schema version for this validator if it cannot be
     * determined from the schema itself
     *
     * @return the default version
     */
    public SchemaVersion getDefaultVersion()
    {
        return defaultVersion;
    }

    /**
     * Sets the default schema version for this validator if it cannot be
     * determined from the schema itself
     *
     * @param defaultVersion the default version
     */
    public void setDefaultVersion(final SchemaVersion defaultVersion)
    {
        ctxlock.writeLock().lock();

        try {
            this.defaultVersion = defaultVersion;
            provider.setDefaultVersion(defaultVersion);
        } finally {
            ctxlock.writeLock().unlock();
        }
    }


    /**
     * Set a feature for this validator
     *
     * @param feature the feature to set
     */
    public void setFeature(final ValidationFeature feature)
    {
        if (features.contains(feature))
            return;

        ctxlock.writeLock().lock();

        try {
            features.add(feature);
            switch (feature) {
                case FAIL_FAST:
                    reports = new ReportFactory(true);
                    break;
                case SKIP_SCHEMACHECK:
                    buildFactories(true);
                    break;
            }
            context = new ValidationContext(factories, provider, reports);
        } finally {
            ctxlock.writeLock().unlock();
        }
    }

    /**
     * Remove a feature from this validator
     *
     * @param feature the feature to remove
     */
    public void removeFeature(final ValidationFeature feature)
    {
        if (!features.contains(feature))
            return;

        ctxlock.writeLock().lock();

        try {
            features.remove(feature);
            switch (feature) {
                case FAIL_FAST:
                    reports = new ReportFactory(false);
                    break;
                case SKIP_SCHEMACHECK:
                    buildFactories(true);
            }
            context = new ValidationContext(factories, provider, reports);
        } finally {
            ctxlock.writeLock().unlock();
        }
    }

    /**
     * Unregister validators for a particular keyword
     *
     * <p>This will unregister both the {@link SyntaxValidator} and {@link
     * KeywordValidator} for this keyword. Note that calling this method
     * will effectively render the keyword <b>unrecognized</b>,
     * which means schemas bearing this particular keyword will be considered
     * <b>INVALID</b>. This is why this method should always be called
     * before, and paired with,
     * {@link #registerValidator(String, SyntaxValidator, KeywordValidator,
     * NodeType...)}
     * unless you really mean to reduce the subset of recognized keywords.</p>
     *
     * @param keyword the keyword to unregister
     * @throws IllegalArgumentException if keyword is null
     */
    public void unregisterValidator(final String keyword)
    {
        if (keyword == null)
            throw new IllegalArgumentException("keyword is null");

        ctxlock.writeLock().lock();

        try {
            factories.get(defaultVersion).unregisterValidator(keyword);
        } finally {
            ctxlock.writeLock().unlock();
        }
    }

    /**
     * Register a new set of validators for a particular keyword
     *
     * <p>Note that if {@code null} is passed to validators,
     * then validation will always succeed. Be particularly careful if you
     * pass null as an argument to the syntax validator and not the keyword
     * validator, as the primary role of a syntax validator is to ensure that
     * the keyword validator have the data it expects in the schema!</p>
     *
     * @param keyword the keyword to register
     * @param sv the {@link SyntaxValidator} to register for this keyword
     * @param kv the {@link KeywordValidator} to register for this keyword
     * @param types the list of primitive types the keyword validator applies to
     * @throws IllegalArgumentException if keyword is null
     */
    public void registerValidator(final String keyword,
        final SyntaxValidator sv, final KeywordValidator kv,
        final NodeType... types)
    {
        if (keyword == null)
            throw new IllegalArgumentException("keyword is null");

        ctxlock.writeLock().lock();

        try {
            factories.get(defaultVersion).registerValidator(keyword, sv, kv,
                types);
        } finally {
            ctxlock.writeLock().unlock();
        }
    }

    /**
     * Register a new {@link URIHandler} for a given scheme
     *
     * @param scheme the scheme
     * @param handler the handler
     * @throws IllegalArgumentException the provided scheme is null
     *
     * @see URIHandlerFactory#registerHandler(String, URIHandler)
     */
    public void registerURIHandler(final String scheme, final URIHandler handler)
    {
        if (scheme == null)
            throw new IllegalArgumentException("scheme is null");

        ctxlock.writeLock().lock();

        try {
            provider.registerHandler(scheme, handler);
        } finally {
            ctxlock.writeLock().unlock();
        }
    }

    /**
     * Unregister the handler for a given scheme
     *
     * @param scheme the victim
     * @throws IllegalArgumentException the provided scheme is null
     *
     * @see URIHandlerFactory#unregisterHandler(String)
     */
    public void unregisterURIHandler(final String scheme)
    {
        if (scheme == null)
            throw new IllegalArgumentException("scheme is null");

        ctxlock.writeLock().lock();
        try {
            provider.unregisterHandler(scheme);
        } finally {
            ctxlock.writeLock().unlock();
        }
    }

    /**
     * Validate an instance against the schema
     *
     * @param instance the instance to validate
     * @return the validation report
     * @throws JsonValidationFailureException on validation failure,
     * if {@link ValidationFeature#FAIL_FAST} is set
     */
    public ValidationReport validate(final JsonNode instance)
        throws JsonValidationFailureException
    {
        ctxlock.readLock().lock();

        try {
            final Validator validator = context.getValidator(instance);
            return validator.validate(context, instance);
        } finally {
            ctxlock.readLock().unlock();
        }
    }

    /**
     * Validate an instance against a subschema of a given schema
     *
     * <p>If, for instance, you have a schema defined as:</p>
     * <pre>
     *     {
     *         "schema1": { "some": "schema here" },
     *         "schema2": { "another": "schema here" }
     *     }
     * </pre>
     * <p>then you will be able to validate instances against {@code
     * schema1} by invoking this method with {@code #/schema1} as the path</p>
     *
     * @param path the path to the actual schema
     * @param instance the instance to validate
     * @return a report of the validation
     * @throws JsonValidationFailureException on validation failure,
     * if {@link ValidationFeature#FAIL_FAST} is set
     */
    public ValidationReport validate(final String path, final JsonNode instance)
        throws JsonValidationFailureException
    {
        final JsonPointer pointer = new JsonPointer(path);

        ctxlock.readLock().lock();

        try {
            final Validator validator = context.getValidator(pointer, instance);
            return validator.validate(context, instance);
        } finally {
            ctxlock.readLock().unlock();
        }
    }

    public ValidationReport validateSchema()
        throws JsonValidationFailureException
    {
        return context.validateSchema();
    }
}
