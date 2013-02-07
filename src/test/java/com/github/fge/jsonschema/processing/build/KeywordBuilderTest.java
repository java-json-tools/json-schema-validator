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

package com.github.fge.jsonschema.processing.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.processing.ValidationDigest;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class KeywordBuilderTest
{
    private static final String K1 = "k1";
    private static final String K2 = "k2";
    private static final String CHALLENGED = "challenged";

    private final KeywordBuilder keywordBuilder;

    public KeywordBuilderTest()
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

        keywordBuilder = new KeywordBuilder(builder.freeze());
    }

    @Test
    public void challengedConstructorRaisesAnException()
    {
        final Map<String, JsonNode> digests = Maps.newTreeMap();
        digests.put(K1, JacksonUtils.nodeFactory().nullNode());
        digests.put(CHALLENGED, JacksonUtils.nodeFactory().nullNode());

        final ValidationDigest digest = new ValidationDigest(null, digests);
        final ProcessingReport report = mock(ProcessingReport.class);

        try {
            keywordBuilder.process(report, digest);
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

        final ValidationDigest digest = new ValidationDigest(null, digests);
        final ProcessingReport report = mock(ProcessingReport.class);

        final FullValidationContext context
            = keywordBuilder.process(report, digest);

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

        final ValidationDigest digest = new ValidationDigest(null, digests);
        final ProcessingReport report = mock(ProcessingReport.class);

        final FullValidationContext context
            = keywordBuilder.process(report, digest);

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
        public void validate(
            final Processor<ValidationData, ProcessingReport> processor,
            final ProcessingReport report, final ValidationData data)
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
            final Processor<ValidationData, ProcessingReport> processor,
            final ProcessingReport report, final ValidationData data)
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
            final Processor<ValidationData, ProcessingReport> processor,
            final ProcessingReport report, final ValidationData data)
            throws ProcessingException
        {
        }
    }

}
