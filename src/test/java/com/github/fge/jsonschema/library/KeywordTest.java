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

package com.github.fge.jsonschema.library;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.MinItemsValidator;
import com.github.fge.jsonschema.keyword.validator.draftv4.NotValidator;
import com.github.fge.jsonschema.messages.MessageBundle;
import com.github.fge.jsonschema.messages.ValidationBundles;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class KeywordTest
{
    private static final MessageBundle BUNDLE
        = ValidationBundles.VALIDATION_CFG;

    private KeywordBuilder builder;

    @BeforeMethod
    public void initBuilder()
    {
        builder = Keyword.newBuilder("foo");
    }

    @Test
    public void cannotCreateKeywordWithNullName()
    {
        try {
            Keyword.newBuilder(null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullName"));
        }
    }

    @Test
    public void cannotInjectNullSyntaxChecker()
    {
        try {
            builder.withSyntaxChecker(null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message)
                .hasMessage(BUNDLE.getString("nullSyntaxChecker"));
        }
    }

    @Test
    public void cannotInjectNullDigester()
    {
        try {
            builder.withDigester(null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullDigester"));
        }
    }

    @Test
    public void identityDigesterTypesMustNotBeNull()
    {
        try {
            builder.withIdentityDigester(null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullType"));
        }

        try {
            builder.withIdentityDigester(NodeType.ARRAY, NodeType.OBJECT, null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullType"));
        }
    }

    @Test
    public void simpleDigesterTypesMustNotBeNull()
    {
        try {
            builder.withSimpleDigester(null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullType"));
        }

        try {
            builder.withSimpleDigester(NodeType.ARRAY, NodeType.OBJECT, null);
            fail("No exception trown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullType"));
        }
    }

    @Test
    public void inappropriateConstructorThrowsAppropriateError()
    {
        try {
            builder.withValidatorClass(DummyValidator.class);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message)
                .hasMessage(BUNDLE.getString("noAppropriateConstructor"));
        }
    }

    @Test
    public void whenValidatorIsPresentSyntaxCheckerMustBeThere()
    {
        try {
            builder.withValidatorClass(MinItemsValidator.class).freeze();
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("noChecker"));
        }
    }

    @Test
    public void validatorClassMustBePairedWithDigester()
    {
        try {
            builder.withSyntaxChecker(mock(SyntaxChecker.class))
                .withValidatorClass(NotValidator.class).freeze();
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message)
                .hasMessage(BUNDLE.getString("malformedKeyword"));
        }
    }

    public static class DummyValidator
        implements KeywordValidator
    {
        @Override
        public void validate(
            final Processor<FullData, FullData> processor,
            final ProcessingReport report, final FullData data)
            throws ProcessingException
        {
        }
    }
}
