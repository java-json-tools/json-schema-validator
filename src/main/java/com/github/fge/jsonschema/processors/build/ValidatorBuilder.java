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

package com.github.fge.jsonschema.processors.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullValidationContext;
import com.github.fge.jsonschema.processors.data.ValidationDigest;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.SortedMap;

public final class ValidatorBuilder
    implements Processor<ValidationDigest, FullValidationContext>
{
    private static final String ERRMSG = "failed to build keyword validator";

    private final Map<String, Constructor<? extends KeywordValidator>>
        constructors;

    public ValidatorBuilder(
        final Dictionary<Constructor<? extends KeywordValidator>> dict)
    {
        constructors = ImmutableMap.copyOf(dict.asMap());
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
    public FullValidationContext process(final ProcessingReport report,
        final ValidationDigest input)
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
        return new FullValidationContext(input.getContext(), map.values());
    }

    private static KeywordValidator buildKeyword(
        final Constructor<? extends KeywordValidator> constructor,
        final JsonNode node
    )
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
