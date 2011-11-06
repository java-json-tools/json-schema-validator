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

package eel.kitchen.jsonschema.container;

import eel.kitchen.jsonschema.base.MatchAllValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A specialized {@link ContainerValidator} for object nodes.
 */
public final class ObjectValidator
    extends ContainerValidator
{
    /**
     * The contents of the {@code properties} keyword, as a {@link SortedMap}
     */
    private final SortedMap<String, JsonNode> properties
        = new TreeMap<String, JsonNode>();

    /**
     * The contents of the {@code patternProperties} keyword
     */
    private final SortedMap<String, JsonNode> patternProperties
        = new TreeMap<String, JsonNode>();

    /**
     * The content of {@code additionalProperties}
     */
    private JsonNode additionalProperties;

    public ObjectValidator(final Validator validator,
        final ValidationContext context, final JsonNode instance)
    {
        super(validator, context, instance);
    }

    /**
     * <p>Fills in {@link #properties}, {@link #patternProperties} and {@link
     * #additionalProperties} for use by {@link
     * #getValidator(String, JsonNode)}.</p>
     */
    @Override
    protected void buildPathProvider()
    {
        final JsonNode schema = context.getSchemaNode();

        JsonNode node;

        node = schema.path("properties");

        if (node.isObject())
            properties.putAll(CollectionUtils.toMap(node.getFields()));

        node = schema.path("patternProperties");

        if (node.isObject())
            patternProperties.putAll(CollectionUtils.toMap(node.getFields()));

        node = schema.path("additionalProperties");

        additionalProperties = node.isObject() ? node : EMPTY_SCHEMA;
    }

    /**
     * <p>Spawns a validator for a child node. Unlike arrays,
     * this is not a straight one-to-one relation:</p>
     * <ul>
     *     <li>if a property matches exactly (ie, there is a matching key in
     *     {@link #properties}, then the child must be validated against the
     *     corresponding schema,
     *     </li>
     *     <li>but it must also be validated against <i>all</i> schemas in
     *     {@link #patternProperties} for which the regex matches the child
     *     property name;
     *     </li>
     *     <li>only if none of the above is true, it must be matched against
     *     the content of {@link #additionalProperties}.
     *     </li>
     * </ul>
     * @param path the path of the child node
     * @param child the child node
     * @return the validator for this child node
     */
    @Override
    protected Validator getValidator(final String path, final JsonNode child)
    {
        final Set<JsonNode> schemas = new HashSet<JsonNode>();

        if (properties.containsKey(path))
            schemas.add(properties.get(path));

        for (final String pattern: patternProperties.keySet())
            if (RhinoHelper.regMatch(pattern, path))
                schemas.add(patternProperties.get(pattern));

        final ValidationContext ctx;

        if (schemas.size() <= 1) {
            final JsonNode subSchema = schemas.isEmpty() ? additionalProperties
                : schemas.iterator().next();
            ctx = context.createContext(path, subSchema);
            return ctx.getValidator(child);
        }

        final Set<Validator> validators = new HashSet<Validator>();

        ctx = context.createContext(path, EMPTY_SCHEMA);

        ValidationContext tmp;

        for (final JsonNode node: schemas) {
            tmp = ctx.createContext(node);
            validators.add(tmp.getValidator(child));
        }

        return new MatchAllValidator(ctx, validators);
    }

    /**
     * Builds the children validator queue, by grabbing all properties of the
     * instance in alphabetical order (using
     * {@link CollectionUtils#toSortedMap(Iterator)}.
     */
    @Override
    protected void buildQueue()
    {
        final SortedMap<String, JsonNode> map
            = CollectionUtils.toSortedMap(instance.getFields());

        String path;
        JsonNode child;
        Validator v;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            path = entry.getKey();
            child = entry.getValue();
            v = getValidator(path, child);
            queue.add(v);
        }

    }
}
