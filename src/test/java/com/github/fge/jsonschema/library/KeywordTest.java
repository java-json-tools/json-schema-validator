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

package com.github.fge.jsonschema.library;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.MinItemsValidator;
import com.github.fge.jsonschema.keyword.validator.draftv4.NotValidator;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class KeywordTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaConfigurationBundle.class);
    private static final String KEYWORD = "foo";

    private KeywordBuilder builder;

    @BeforeMethod
    public void initBuilder()
    {
        builder = Keyword.newBuilder(KEYWORD);
    }

    @Test
    public void cannotCreateKeywordWithNullName()
    {
        try {
            Keyword.newBuilder(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("nullName"));
        }
    }

    @Test
    public void cannotInjectNullSyntaxChecker()
    {
        try {
            builder.withSyntaxChecker(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.printf("nullSyntaxChecker", KEYWORD));
        }
    }

    @Test
    public void cannotInjectNullDigester()
    {
        try {
            builder.withDigester(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.printf("nullDigester", KEYWORD));
        }
    }

    @Test
    public void identityDigesterTypesMustNotBeNull()
    {
        try {
            builder.withIdentityDigester(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("nullType"));
        }

        try {
            builder.withIdentityDigester(NodeType.ARRAY, NodeType.OBJECT, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("nullType"));
        }
    }

    @Test
    public void simpleDigesterTypesMustNotBeNull()
    {
        try {
            builder.withSimpleDigester(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("nullType"));
        }

        try {
            builder.withSimpleDigester(NodeType.ARRAY, NodeType.OBJECT, null);
            fail("No exception trown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("nullType"));
        }
    }

    @Test
    public void inappropriateConstructorThrowsAppropriateError()
    {
        try {
            builder.withValidatorClass(DummyValidator.class);
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),
                BUNDLE.printf("noAppropriateConstructor", KEYWORD,
                    DummyValidator.class.getCanonicalName()));
        }
    }

    @Test
    public void whenValidatorIsPresentSyntaxCheckerMustBeThere()
    {
        try {
            builder.withValidatorClass(MinItemsValidator.class).freeze();
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf("noChecker", KEYWORD));
        }
    }

    @Test
    public void validatorClassMustBePairedWithDigester()
    {
        try {
            builder.withSyntaxChecker(mock(SyntaxChecker.class))
                .withValidatorClass(NotValidator.class).freeze();
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),
                BUNDLE.printf("malformedKeyword", KEYWORD));
        }
    }

    public static class DummyValidator
        implements KeywordValidator
    {
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
