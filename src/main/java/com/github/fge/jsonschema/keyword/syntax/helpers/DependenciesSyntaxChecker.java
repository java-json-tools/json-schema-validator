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

package com.github.fge.jsonschema.keyword.syntax.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.keyword.syntax.AbstractSyntaxChecker;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.equivalence.JsonSchemaEquivalence;
import com.google.common.base.Equivalence;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

/**
 * Helper class for syntax checking of draft v4 and v3 {@code dependencies}
 *
 * <p>The validation check also fills the JSON Pointer list with the
 * appropriate paths when schema dependencies are encountered.</p>
 */
public abstract class DependenciesSyntaxChecker
    extends AbstractSyntaxChecker
{
    /**
     * JSON Schema equivalence
     */
    protected static final Equivalence<JsonNode> EQUIVALENCE
        = JsonSchemaEquivalence.getInstance();

    /**
     * Valid types for one dependency value
     */
    protected final EnumSet<NodeType> dependencyTypes;

    /**
     * Protected constructor
     *
     * @param depTypes valid types for one dependency value
     */
    protected DependenciesSyntaxChecker(final NodeType... depTypes)
    {
        super("dependencies", NodeType.OBJECT);
        dependencyTypes = EnumSet.of(NodeType.OBJECT, depTypes);
    }

    @Override
    protected final void checkValue(final Collection<JsonPointer> pointers,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree);
        final Map<String, JsonNode> map = Maps.newTreeMap();
        map.putAll(JacksonUtils.asMap(node));

        String key;
        JsonNode value;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (value.isObject())
                pointers.add(JsonPointer.of(keyword, key));
            else
                checkDependency(report, entry.getKey(), tree);
        }

    }

    /**
     * Check one dependency which is not a schema dependency
     *
     * @param report the processing report to use
     * @param name the property dependency name
     * @param tree the schema
     * @throws InvalidSchemaException keyword is invalid
     */
    protected abstract void checkDependency(final ProcessingReport report,
        final String name, final SchemaTree tree)
        throws ProcessingException;
}
