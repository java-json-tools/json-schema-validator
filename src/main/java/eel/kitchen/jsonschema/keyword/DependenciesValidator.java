/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.keyword;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Keyword validation for the {@code dependencies} keyword (draft section 5
 * .8). It supports both simple dependencies (ie, other property names) or
 * schema dependencies.
 */
public final class DependenciesValidator
    extends KeywordValidator
{

    /**
     * The list of fields in the instance to validate
     */
    private final SortedSet<String> instanceFields
        = new TreeSet<String>();

    /**
     * The list of simple (ie, non schema) dependencies in the schema
     */
    private final Map<String, Collection<String>> simpleDependencies
        = new HashMap<String, Collection<String>>();

    public DependenciesValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);

        instanceFields.addAll(CollectionUtils.toSet(instance.getFieldNames()));

        setUp();
    }

    /**
     * Called from the constructor. Fills the {@link #simpleDependencies}
     * map, and/or {@link #queue} in the event of schema dependencies
     */
    private void setUp()
    {
        final JsonNode dependenciesNode
            = context.getSchemaNode().get("dependencies");
        final Set<String> deps
            = CollectionUtils.toSet(dependenciesNode.getFieldNames());

        deps.retainAll(instanceFields);

        JsonNode node;
        for (final String dep: deps) {
            node = dependenciesNode.get(dep);
            switch (NodeType.getNodeType(node)) {
                case STRING:
                    simpleDependencies.put(dep,
                        Arrays.asList(node.getTextValue()));
                    break;
                case ARRAY:
                    final Set<String> set = new HashSet<String>();
                    for (final JsonNode element: node)
                        set.add(element.getTextValue());
                    simpleDependencies.put(dep, set);
                    break;
                case OBJECT:
                    final ValidationContext ctx = context.createContext(node);
                    queue.add(ctx.getValidator(instance));
                    break;
                default:
                    throw new RuntimeException("How did I even get there???");
            }
        }
    }

    /**
     * Validate the instance: check for simple dependencies first,
     * and if any, for schema dependencies
     *
     * @return the validation report
     */
    @Override
    public ValidationReport validate()
    {
        final SortedSet<String> set = new TreeSet<String>();

        for (final String field: instanceFields) {
            if (!simpleDependencies.containsKey(field))
                continue;
            set.clear();
            set.addAll(simpleDependencies.get(field));
            set.removeAll(instanceFields);
            if (!set.isEmpty())
                report.addMessage("property " + field + " is missing "
                    + "dependencies " + set);
        }

        while (report.isSuccess() && hasMoreElements())
            report.mergeWith(nextElement().validate());

        queue.clear();
        return report;
    }
}
