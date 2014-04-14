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
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.processors.data.SchemaDigest;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.SortedMap;

/**
 * Keyword builder processor
 *
 * <p>This takes a {@link SchemaDigest} as an input and outputs a {@link
 * ValidatorList}. The main processor, {@link ValidationProcessor}, then uses
 * this validator list to perform actual instance validation.</p>
 *
 * @see ValidationProcessor
 */
public final class ValidatorBuilder
    implements Processor<SchemaDigest, ValidatorList>
{
    private static final String ERRMSG = "failed to build keyword validator";

    private final Map<String, Constructor<? extends KeywordValidator>>
        constructors;

    public ValidatorBuilder(final Library library)
    {
        constructors = library.getValidators().entries();
    }

    public ValidatorBuilder(
        final Dictionary<Constructor<? extends KeywordValidator>> dict)
    {
        constructors = dict.entries();
    }

    /**
     * Process the input
     *
     * @param report the report to use while processing
     * @param input the input for this processor
     * @return the output
     * @throws ProcessingException processing failed
     */
    @Override
    public ValidatorList process(final ProcessingReport report,
        final SchemaDigest input)
        throws ProcessingException
    {
        final SortedMap<String, KeywordValidator> map = Maps.newTreeMap();

        String keyword;
        JsonNode digest;
        KeywordValidator validator;
        Constructor<? extends KeywordValidator> constructor;

        for (final Map.Entry<String, JsonNode> entry:
            input.getDigests().entrySet()) {
            keyword = entry.getKey();
            digest = entry.getValue();
            constructor = constructors.get(keyword);
            validator = buildKeyword(constructor, digest);
            map.put(keyword, validator);
        }
        return new ValidatorList(input.getContext(), map.values());
    }

    private static KeywordValidator buildKeyword(
        final Constructor<? extends KeywordValidator> constructor,
        final JsonNode node)
        throws ProcessingException
    {
        try {
            return constructor.newInstance(node);
        } catch (InstantiationException e) {
            throw new ProcessingException(ERRMSG, e);
        } catch (IllegalAccessException e) {
            throw new ProcessingException(ERRMSG, e);
        } catch (InvocationTargetException e) {
            throw new ProcessingException(ERRMSG, e);
        }
    }

    @Override
    public String toString()
    {
        return "validator builder";
    }
}
