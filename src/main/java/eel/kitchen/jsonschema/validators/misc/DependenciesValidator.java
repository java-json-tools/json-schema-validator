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

package eel.kitchen.jsonschema.validators.misc;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.IterableJsonNode;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DependenciesValidator
    extends AbstractValidator
{
    private final Map<String, Set<String>> dependencies
        = new HashMap<String, Set<String>>();

    public DependenciesValidator()
    {
        registerField("dependencies", NodeType.OBJECT);
    }

    @Override
    protected boolean doSetup()
    {
        if (!super.doSetup())
            return false;

        dependencies.clear();

        final JsonNode depsNode = schema.get("dependencies");
        if (depsNode == null)
            return true;

        final IterableJsonNode inode = new IterableJsonNode(depsNode);

        String fieldName;
        Set<String> set;

        for (final Map.Entry<String, JsonNode> entry: inode) {
            fieldName = entry.getKey();
            try {
                set = computeOneDependency(entry.getValue());
            } catch (MalformedJasonSchemaException e) {
                messages.add(e.getMessage());
                return false;
            }
            set.remove(fieldName);
            dependencies.put(fieldName, set);
        }

        return true;
    }

    private static Set<String> computeOneDependency(final JsonNode node)
        throws MalformedJasonSchemaException
    {
        final Set<String> ret = new HashSet<String>();

        if (node.isTextual()) {
            ret.add(node.getTextValue());
            return ret;
        }

        if (!node.isArray())
            throw new MalformedJasonSchemaException("dependency value should "
                + "be a string or an array");

        for (final JsonNode element: node) {
            if (!element.isTextual())
                throw new MalformedJasonSchemaException("dependency "
                    + "array elements should be strings");
            ret.add(element.getTextValue());
        }

        return ret;
    }

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        boolean ret = true;

        final Set<String>
            checks = new HashSet<String>(dependencies.keySet()),
            existing = CollectionUtils.toSet(node.getFieldNames());

        checks.retainAll(existing);

        final Set<String> deps = new HashSet<String>();

        for (final String field: checks) {
            deps.clear();
            deps.addAll(dependencies.get(field));
            deps.removeAll(existing);
            if (deps.isEmpty())
                continue;
            ret = false;
            for (final String missing: deps)
                messages.add(String.format("property %s depends on %s, "
                    + "but the latter was not found", field, missing));
        }

        return ret;
    }
}
