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

package eel.kitchen.jsonschema.v2.keyword;

import eel.kitchen.jsonschema.v2.schema.Schema;
import eel.kitchen.jsonschema.v2.schema.SchemaFactory;
import eel.kitchen.jsonschema.v2.schema.ValidationMode;
import eel.kitchen.jsonschema.v2.schema.ValidationState;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static eel.kitchen.jsonschema.v2.schema.ValidationMode.*;

public final class DependenciesKeywordValidator
    extends ExtensibleKeywordValidator
{
    private final JsonNode dependencies;

    private final Map<String, Set<String>> simpleDependencies
        = new HashMap<String, Set<String>>();

    private final Map<String, JsonNode> schemaDependencies
        = new HashMap<String, JsonNode>();

    public DependenciesKeywordValidator(final JsonNode schema)
    {
        super(schema);
        dependencies = schema.get("dependencies");
        setup();
    }

    private void setup()
    {
        final Iterator<Map.Entry<String, JsonNode>> iterator
            = dependencies.getFields();

        Set<String> set;
        Map.Entry<String, JsonNode> entry;
        String field;
        JsonNode node;

        while (iterator.hasNext()) {
            entry = iterator.next();
            field = entry.getKey();
            node = entry.getValue();
            set = new HashSet<String>();
            switch (NodeType.getNodeType(node)) {
                case STRING:
                    set.add(node.getTextValue());
                    simpleDependencies.put(field, set);
                    break;
                case OBJECT:
                    schemaDependencies.put(field, node);
                    break;
                case ARRAY:
                    for (final JsonNode element: node)
                        set.add(element.getTextValue());
                    simpleDependencies.put(field, set);
                    break;
                default:
                    throw new RuntimeException("How did I even get there???");
            }
        }
    }

    @Override
    public void validate(final ValidationState state, final JsonNode node)
    {
        final Set<String> allFields
            = CollectionUtils.toSet(node.getFieldNames());

        final Map<String, Set<String>> map
            = new HashMap<String, Set<String>>(simpleDependencies);

        map.keySet().retainAll(allFields);

        final Set<String> deps = new HashSet<String>();

        String field;

        for (final Map.Entry<String, Set<String>> entry: map.entrySet()) {
            deps.clear();
            field = entry.getKey();
            deps.addAll(entry.getValue());
            deps.removeAll(allFields);
            if (!deps.isEmpty())
                state.addMessage("property " + field + " is missing "
                    + "dependencies");
        }

        if (schemaDependencies.isEmpty())
            return;

        buildNext(state.getFactory());
        state.setNextSchema(nextSchema);
    }

    @Override
    protected void buildNext(final SchemaFactory factory)
    {
        final EnumSet<ValidationMode> mode
            = EnumSet.of(VALIDATE_ALL, VALIDATE_NORMAL);

        final Set<Schema> set = new HashSet<Schema>();

        for (final Map.Entry<String, JsonNode> entry: schemaDependencies.entrySet())
            set.add(buildOneSchema(factory, entry));

        nextSchema = factory.buildSchemaFromSet(mode, set);
    }

    private static Schema buildOneSchema(final SchemaFactory factory,
        final Map.Entry<String, JsonNode> entry)
    {
        final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

        final Schema notA, orB;

        final ObjectNode requiredNode = nodeFactory.objectNode();

        requiredNode.put("required", true);

        final ObjectNode propertyNode = nodeFactory.objectNode();

        propertyNode.put(entry.getKey(), requiredNode);

        final ObjectNode schemaNode = nodeFactory.objectNode();

        schemaNode.put("properties", propertyNode);

        notA = factory.buildSingleSchema(VALIDATE_MATCHFAIL, schemaNode);

        orB = factory.buildSingleSchema(VALIDATE_NORMAL, entry.getValue());

        final Set<Schema> set = new HashSet<Schema>();

        set.add(notA);
        set.add(orB);

        final EnumSet<ValidationMode> mode
            = EnumSet.of(VALIDATE_ANY, VALIDATE_NORMAL);

        return factory.buildSchemaFromSet(mode, set);
    }
}
