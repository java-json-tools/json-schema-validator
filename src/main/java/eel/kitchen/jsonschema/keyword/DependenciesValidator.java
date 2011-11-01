/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import eel.kitchen.jsonschema.ValidatorFactory;
import eel.kitchen.jsonschema.base.CombinedValidator;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DependenciesValidator
    extends CombinedValidator
{
    private final JsonNode dependencies;

    private final Set<String> instanceFields
        = new HashSet<String>();

    private final Map<String, Collection<String>> simpleDependencies
        = new HashMap<String, Collection<String>>();

    public DependenciesValidator(final ValidatorFactory factory,
        final JsonNode schema, final JsonNode instance)
    {
        super(factory, schema, instance);

        dependencies = schema.get("dependencies");
        instanceFields.addAll(CollectionUtils.toSet(instance.getFieldNames()));

        setUp();
    }

    private void setUp()
    {
        final Set<String> deps
            = CollectionUtils.toSet(dependencies.getFieldNames());

        deps.retainAll(instanceFields);

        JsonNode node;
        for (final String dep: deps) {
            node = instance.get(dep);
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
                    queue.add(factory.getValidator(node, instance));
                    break;
                default:
                    throw new RuntimeException("How did I even get there???");
            }
        }
    }

    @Override
    public ValidationReport validate()
    {
        final Set<String> set = new HashSet<String>();

        for (final String field: instanceFields) {
            if (!simpleDependencies.containsKey(field))
                continue;
            set.clear();
            set.addAll(simpleDependencies.get(field));
            set.removeAll(instanceFields);
            if (!set.isEmpty()) {
                report.addMessage("property " + field + " is missing "
                    + "dependencies " + set);
                break;
            }
        }

        while (report.isSuccess() && hasMoreElements())
            report.mergeWith(nextElement().validate());

        queue.clear();
        return report;
    }
}
