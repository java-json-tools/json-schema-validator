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

package com.github.fge.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.github.fge.Frozen;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.RefResolver;
import com.github.fge.jsonschema.core.load.SchemaLoader;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.processing.CachingProcessor;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.processing.ProcessorMap;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.ReportProvider;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator;
import com.github.fge.jsonschema.processors.validation.SchemaContextEquivalence;
import com.github.fge.jsonschema.processors.validation.ValidationChain;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.base.Function;

import javax.annotation.concurrent.Immutable;
import java.util.Map;

/**
 * The main validator provider
 *
 * <p>From an instance of this factory, you can obtain the following:</p>
 *
 * <ul>
 *     <li>a {@link SyntaxValidator}, to validate schemas;</li>
 *     <li>a {@link JsonValidator}, to validate an instance against a schema;
 *     </li>
 *     <li>a {@link JsonSchemaImpl}, to validate instances against a fixed schema.
 *     </li>
 * </ul>
 *
 * @see JsonSchemaFactoryBuilder
 */
@Immutable
public final class JsonSchemaFactory
    implements Frozen<JsonSchemaFactoryBuilder>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaConfigurationBundle.class);
    private static final MessageBundle CORE_BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final Function<SchemaContext, JsonRef> FUNCTION
        = new Function<SchemaContext, JsonRef>()
    {
        @Override
        public JsonRef apply(final SchemaContext input)
        {
            return input.getSchema().getDollarSchema();
        }
    };

    /*
     * Elements provided by the builder
     */
    final ReportProvider reportProvider;
    final LoadingConfiguration loadingCfg;
    final ValidationConfiguration validationCfg;

    /*
     * Generated elements
     */
    private final SchemaLoader loader;
    private final JsonValidator validator;
    private final SyntaxValidator syntaxValidator;

    /**
     * Return a default factory
     *
     * <p>This default factory has validators for both draft v4 and draft v3. It
     * defaults to draft v4.</p>
     *
     * @return a factory with default settings
     * @see JsonSchemaFactoryBuilder#JsonSchemaFactoryBuilder()
     */
    public static JsonSchemaFactory byDefault()
    {
        return newBuilder().freeze();
    }

    /**
     * Return a factory builder
     *
     * @return a {@link JsonSchemaFactoryBuilder}
     */
    public static JsonSchemaFactoryBuilder newBuilder()
    {
        return new JsonSchemaFactoryBuilder();
    }

    /**
     * Package private constructor to build a factory out of a builder
     *
     * @param builder the builder
     * @see JsonSchemaFactoryBuilder#freeze()
     */
    JsonSchemaFactory(final JsonSchemaFactoryBuilder builder)
    {
        reportProvider = builder.reportProvider;
        loadingCfg = builder.loadingCfg;
        validationCfg = builder.validationCfg;

        loader = new SchemaLoader(loadingCfg);
        final Processor<SchemaContext, ValidatorList> processor
            = buildProcessor();
        validator = new JsonValidator(loader,
            new ValidationProcessor(validationCfg, processor), reportProvider);
        syntaxValidator = new SyntaxValidator(validationCfg);
    }

    /**
     * Return the main schema/instance validator provided by this factory
     *
     * @return a {@link JsonValidator}
     */
    public JsonValidator getValidator()
    {
        return validator;
    }

    /**
     * Return the syntax validator provided by this factory
     *
     * @return a {@link SyntaxValidator}
     */
    public SyntaxValidator getSyntaxValidator()
    {
        return syntaxValidator;
    }

    /**
     * Build an instance validator tied to a schema
     *
     * <p>Note that the validity of the schema is <b>not</b> checked. Use {@link
     * #getSyntaxValidator()} if you are not sure.</p>
     *
     * @param schema the schema
     * @return a {@link JsonSchema}
     * @throws ProcessingException schema is a {@link MissingNode}
     * @throws NullPointerException schema is null
     */
    public JsonSchema getJsonSchema(final JsonNode schema)
        throws ProcessingException
    {
        BUNDLE.checkNotNull(schema, "nullSchema");
        return validator.buildJsonSchema(schema, JsonPointer.empty());
    }

    /**
     * Build an instance validator tied to a subschema from a main schema
     *
     * <p>Note that the validity of the schema is <b>not</b> checked. Use {@link
     * #getSyntaxValidator()} if you are not sure.</p>
     *
     * @param schema the schema
     * @param ptr a JSON Pointer as a string
     * @return a {@link JsonSchema}
     * @throws ProcessingException {@code ptr} is not a valid JSON Pointer, or
     * resolving the pointer against the schema leads to a {@link MissingNode}
     * @throws NullPointerException schema is null, or pointer is null
     */
    public JsonSchema getJsonSchema(final JsonNode schema, final String ptr)
        throws ProcessingException
    {
        BUNDLE.checkNotNull(schema, "nullSchema");
        CORE_BUNDLE.checkNotNull(ptr, "nullPointer");
        final JsonPointer pointer;
        try {
            pointer = new JsonPointer(ptr);
            return validator.buildJsonSchema(schema, pointer);
        } catch (JsonPointerException ignored) {
            // Cannot happen
        }
        throw new IllegalStateException("How did I get there??");
    }

    /**
     * Build an instance validator out of a schema loaded from a URI
     *
     * @param uri the URI
     * @return a {@link JsonSchema}
     * @throws ProcessingException failed to load from this URI
     * @throws NullPointerException URI is null
     */
    public JsonSchema getJsonSchema(final String uri)
        throws ProcessingException
    {
        CORE_BUNDLE.checkNotNull(uri, "nullURI");
        return validator.buildJsonSchema(uri);
    }

    /**
     * Return the raw validation processor
     *
     * <p>This will allow you to chain the full validation processor with other
     * processors of your choice. Useful if, for instance, you wish to add post
     * checking which JSON Schema cannot do by itself.</p>
     *
     * @return the processor.
     */
    public Processor<FullData, FullData> getProcessor()
    {
        return validator.getProcessor();
    }

    /**
     * Return a thawed instance of that factory
     *
     * @return a {@link JsonSchemaFactoryBuilder}
     * @see JsonSchemaFactoryBuilder#JsonSchemaFactoryBuilder(JsonSchemaFactory)
     */
    @Override
    public JsonSchemaFactoryBuilder thaw()
    {
        return new JsonSchemaFactoryBuilder(this);
    }

    private Processor<SchemaContext, ValidatorList> buildProcessor()
    {
        final RefResolver resolver = new RefResolver(loader);

        final Map<JsonRef, Library> libraries = validationCfg.getLibraries();
        final Library defaultLibrary = validationCfg.getDefaultLibrary();
        final ValidationChain defaultChain
            = new ValidationChain(resolver, defaultLibrary, validationCfg);
        final ProcessorMap<JsonRef, SchemaContext, ValidatorList> map
            = new ProcessorMap<JsonRef, SchemaContext, ValidatorList>(FUNCTION);
        map.setDefaultProcessor(defaultChain);

        JsonRef ref;
        ValidationChain chain;

        for (final Map.Entry<JsonRef, Library> entry: libraries.entrySet()) {
            ref = entry.getKey();
            chain = new ValidationChain(resolver, entry.getValue(),
                validationCfg);
            map.addEntry(ref, chain);
        }

        final Processor<SchemaContext, ValidatorList> processor
            = map.getProcessor();
        return new CachingProcessor<SchemaContext, ValidatorList>(processor,
            SchemaContextEquivalence.getInstance(), validationCfg.getCacheSize());
    }
}
