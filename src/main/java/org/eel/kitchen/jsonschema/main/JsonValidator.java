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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.main;


import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.factories.ValidatorFactory;
import org.eel.kitchen.jsonschema.uri.URIHandler;
import org.eel.kitchen.jsonschema.uri.URIHandlerFactory;
import org.eel.kitchen.util.JsonLoader;
import org.eel.kitchen.util.JsonPointer;
import org.eel.kitchen.util.SchemaVersion;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The main interface to use for JSON Schema validation
 *
 * <p>Apart from validating JSON instances, it has several other roles:</p>
 * <ul>
 *     <li>determining which JSON Schema version is used by default,
 *     if schemas do not provide a {@code $schema} keyword;</li>
 *     <li>registering/unregistering validators against a particular schema
 *     version;</li>
 *     <li>registering/unregistering a {@link URIHandler} for a particular
 *     scheme.
 *     </li>
 * </ul>
 *
 * @see JsonLoader
 * @see ValidationContext
 */
public final class JsonValidator
{
    private final ValidationConfig cfg;

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
     *
     * @param cfg
     * @param schema the root schema to use for validation
     * @throws JsonValidationFailureException the initial JSON node is not a
     * schema
     */
    public JsonValidator(final ValidationConfig cfg, final JsonNode schema)
        throws JsonValidationFailureException
    {
        this.cfg = cfg;
        provider = new SchemaProvider(cfg.getDefaultVersion(), schema);
        reports = new ReportFactory(false);
        buildFactories(false);
        context = new ValidationContext(factories, provider, reports);
    }

    private void buildFactories(final boolean skipSyntax)
    {
        ValidatorBundle bundle;
        for (final SchemaVersion version: SchemaVersion.values()) {
            bundle = cfg.getBundles().get(version);
            factories.put(version, new ValidatorFactory(bundle, skipSyntax));
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
     * <p>If, for instance, you have a JSON document such as:</p>
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

    /**
     * Validate the registered schema
     *
     * @return a validation report
     * @throws JsonValidationFailureException if reporting has been set to
     * throw this exception instead of collecting messages
     */
    public ValidationReport validateSchema()
        throws JsonValidationFailureException
    {
        return context.validateSchema();
    }
}
