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

package com.github.fge.jsonschema.processors.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.data.SchemaDigest;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ValidatorBuilderTest
{
    private static final String K1 = "k1";
    private static final String K2 = "k2";
    private static final String CHALLENGED = "challenged";

    private final ValidatorBuilder validatorBuilder;

    public ValidatorBuilderTest()
        throws NoSuchMethodException
    {
        final DictionaryBuilder<Constructor<? extends KeywordValidator>>
            builder = Dictionary.newBuilder();

        Constructor<? extends KeywordValidator> constructor;

        constructor = Keyword1.class .getConstructor(JsonNode.class);
        builder.addEntry(K1, constructor);
        constructor = Keyword2.class.getConstructor(JsonNode.class);
        builder.addEntry(K2, constructor);
        constructor = Challenged.class.getConstructor(JsonNode.class);
        builder.addEntry(CHALLENGED, constructor);

        validatorBuilder = new ValidatorBuilder(builder.freeze());
    }

    @Test
    public void challengedConstructorRaisesAnException()
    {
        final Map<String, JsonNode> digests = Maps.newTreeMap();
        digests.put(K1, JacksonUtils.nodeFactory().nullNode());
        digests.put(CHALLENGED, JacksonUtils.nodeFactory().nullNode());

        final SchemaDigest digest = new SchemaDigest(null, digests);
        final ProcessingReport report = mock(ProcessingReport.class);

        try {
            validatorBuilder.process(report, digest);
            fail("No exception thrown??");
        } catch (ProcessingException ignored) {
        }
    }

    @Test
    public void onlyRelevantValidatorsAreBuilt()
        throws ProcessingException
    {
        final Map<String, JsonNode> digests = Maps.newTreeMap();
        digests.put(K1, JacksonUtils.nodeFactory().nullNode());

        final SchemaDigest digest = new SchemaDigest(null, digests);
        final ProcessingReport report = mock(ProcessingReport.class);

        final ValidatorList context
            = validatorBuilder.process(report, digest);

        final List<KeywordValidator> list = Lists.newArrayList(context);

        assertEquals(list.size(), 1);
        assertSame(list.get(0).getClass(), Keyword1.class);
    }

    @Test
    public void allRelevantValidatorsAreBuilt()
        throws ProcessingException
    {
        final Map<String, JsonNode> digests = Maps.newTreeMap();
        digests.put(K1, JacksonUtils.nodeFactory().nullNode());
        digests.put(K2, JacksonUtils.nodeFactory().nullNode());

        final SchemaDigest digest = new SchemaDigest(null, digests);
        final ProcessingReport report = mock(ProcessingReport.class);

        final ValidatorList context
            = validatorBuilder.process(report, digest);

        final List<KeywordValidator> list = Lists.newArrayList(context);

        assertEquals(list.size(), 2);
        assertSame(list.get(0).getClass(), Keyword1.class);
        assertSame(list.get(1).getClass(), Keyword2.class);
    }

    public static final class Keyword1
        implements KeywordValidator
    {
        public Keyword1(final JsonNode ignored)
        {
        }

        @Override
        public void validate(final Processor<FullData, FullData> processor,
            final ProcessingReport report, final MessageBundle bundle,
            final FullData data)
            throws ProcessingException
        {
        }
    }

    public static final class Keyword2
        implements KeywordValidator
    {
        public Keyword2(final JsonNode ignored)
        {
        }

        @Override
        public void validate(
            final Processor<FullData, FullData> processor,
            final ProcessingReport report, final MessageBundle bundle,
            final FullData data)
            throws ProcessingException
        {
        }
    }

    public static final class Challenged
        implements KeywordValidator
    {
        public Challenged(final JsonNode ignored)
        {
            throw new ExceptionInInitializerError("moo");
        }

        @Override
        public void validate(
            final Processor<FullData, FullData> processor,
            final ProcessingReport report, final MessageBundle bundle,
            final FullData data)
            throws ProcessingException
        {
        }
    }

}
