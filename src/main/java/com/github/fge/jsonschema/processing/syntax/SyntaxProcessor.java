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

package com.github.fge.jsonschema.processing.syntax;

import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.google.common.base.Equivalence;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

import java.util.Map;

public final class SyntaxProcessor
    implements Processor<ValidationData, ValidationData>
{
    /**
     * Equivalence specifically defined for syntax checking
     *
     * <p>By default, {@link JsonSchemaTree}'s equality is based on the loading
     * JSON Reference and schema. But for syntax checking we need to compare the
     * schema with the current location in it, so that we can accurately report
     * non visited paths and look up these as keys.</p>
     */
    private static final Equivalence<JsonSchemaTree> EQUIVALENCE
        = new Equivalence<JsonSchemaTree>()
    {
        @Override
        protected boolean doEquivalent(final JsonSchemaTree a,
            final JsonSchemaTree b)
        {
            return a.getCurrentRef().equals(b.getCurrentRef())
                && a.getBaseNode().equals(b.getBaseNode());
        }

        @Override
        protected int doHash(final JsonSchemaTree t)
        {
            return 31 * t.getCurrentRef().hashCode()
                + t.getBaseNode().hashCode();
        }
    };

    private final Map<String, SyntaxChecker> checkers = Maps.newTreeMap();

    private final LoadingCache<Equivalence.Wrapper<JsonSchemaTree>,
        SyntaxValidationResults> cache;

    public SyntaxProcessor()
    {
        cache = CacheBuilder.newBuilder().build(loader());
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
    public ValidationData process(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        return input;
    }

    private CacheLoader<Equivalence.Wrapper<JsonSchemaTree>,
        SyntaxValidationResults> loader()
    {
        return new CacheLoader<Equivalence.Wrapper<JsonSchemaTree>, SyntaxValidationResults>()
        {
            @Override
            public SyntaxValidationResults load(
                final Equivalence.Wrapper<JsonSchemaTree> key)
                throws ProcessingException
            {
                return new SyntaxValidationResults();
            }
        };
    }
}
