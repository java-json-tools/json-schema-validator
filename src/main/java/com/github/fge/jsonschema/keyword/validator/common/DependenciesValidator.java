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

package com.github.fge.jsonschema.keyword.validator.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

/*
 * In spite of syntax differences, the digested data is the same, which is why
 * this validator is in common/
 */
/**
 * Keyword validator for draft v4 and v3 {@code dependencies}
 *
 * <p>In spite of syntax differences, the digested data used to build the
 * validator is the same, which is why this validator is located here.</p>
 */
public final class DependenciesValidator
    extends AbstractKeywordValidator
{
    private final Multimap<String, String> propertyDeps;
    private final Set<String> schemaDeps;

    public DependenciesValidator(final JsonNode digest)
    {
        super("dependencies");

        /*
         * Property dependencies
         */
        final ImmutableMultimap.Builder<String, String> mapBuilder
            = ImmutableMultimap.builder();
        final Map<String, JsonNode> map
            = JacksonUtils.asMap(digest.get("propertyDeps"));

        String key;
        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            key = entry.getKey();
            for (final JsonNode element: entry.getValue())
                mapBuilder.put(key, element.textValue());
        }

        propertyDeps = mapBuilder.build();

        /*
         * Schema dependencies
         */
        final ImmutableSet.Builder<String> setBuilder
            = ImmutableSet.builder();

        for (final JsonNode node: digest.get("schemaDeps"))
            setBuilder.add(node.textValue());

        schemaDeps = setBuilder.build();
    }

    @Override
    public void validate(final Processor<FullData, FullData> processor,
        final ProcessingReport report, final FullData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();
        final Set<String> fields = Sets.newHashSet(instance.fieldNames());

        Collection<String> collection;
        Set<String> set;

        for (final String field: propertyDeps.keySet()) {
            if (!fields.contains(field))
                continue;
            collection = propertyDeps.get(field);
            set = Sets.newLinkedHashSet(collection);
            set.removeAll(fields);
            if (!set.isEmpty())
                report.error(newMsg(data).message(MISSING_PROPERTY_DEPS)
                    .put("property", field).put("required", collection)
                    .put("missing", set));
        }

        if (schemaDeps.isEmpty())
            return;

        final SchemaTree tree = data.getSchema();
        FullData newData;
        JsonPointer pointer;

        for (final String field: schemaDeps) {
            if (!fields.contains(field))
                continue;
            pointer = JsonPointer.of(keyword, field);
            newData = data.withSchema(tree.append(pointer));
            processor.process(report, newData);
        }
    }

    @Override
    public String toString()
    {
        return keyword + ": " + propertyDeps.size() + " property dependencies, "
            + schemaDeps.size() + " schema dependencies";
    }
}
