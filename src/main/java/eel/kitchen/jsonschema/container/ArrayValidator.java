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

import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.jsonschema.keyword.AdditionalItemsKeywordValidator;
import org.codehaus.jackson.JsonNode;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@link ContainerValidator} for array instances
 *
 * @see ContainerValidator
 */
public final class ArrayValidator
    extends ContainerValidator
{
    /**
     * The elements in {@code items}, if any
     */
    private final List<JsonNode> items = new LinkedList<JsonNode>();

    /**
     * What is in {@code additionalItems}, or {@code items} if the latter
     * contains a schema
     */
    private JsonNode additionalItems;

    public ArrayValidator(final Validator validator,
        final ValidationContext context, final JsonNode instance)
    {
        super(validator, context, instance);
    }

    /**
     * <p>Builds the {@link #items} list and {@link #additionalItems} schema,
     * with the following algorithm:</p>
     * <ul>
     *     <li>if {@code items} is a schema, leave the items array empty and
     *     affect the value of the node to {@link #additionalItems};
     *     </li>
     *     <li>if it is an array, fill the {@link #items} list with the array
     *     elements; then fill {@link #additionalItems}.</li>
     * </ul>
     * <p>Note that at this stage, as the structure has been validated,
     * it means that {@link AdditionalItemsKeywordValidator} will have done its
     * job and make validation fail if additional items were not permitted.
     * </p>
     */
    @Override
    protected void buildPathProvider()
    {
        final JsonNode schema = context.getSchemaNode();

        JsonNode node = schema.path("items");

        if (node.isObject()) {
            additionalItems = node;
            return;
        }

        if (node.isArray()) {
            for (final JsonNode item: node)
                items.add(item);
        }

        node = schema.path("additionalItems");

        additionalItems = node.isObject() ? node : EMPTY_SCHEMA;
    }

    /**
     * <p>Provide a validator for a subnode. The path is always an integer
     * here. The algorithm is as follows:</p>
     * <ul>
     *     <li>if {@link #items} contains the path, the matching node is used
     *     to build the validator;</li>
     *     <li>otherwise, it is built from {@link #additionalItems}.</li>
     * </ul>
     *
     * @param path the path of the child node
     * @param child the child node
     * @return the matching {@link Validator}
     */
    @Override
    protected Validator getValidator(final String path, final JsonNode child)
    {
        final int index = Integer.parseInt(path);

        final JsonNode node = index < items.size() ? items.get(index)
            : additionalItems;

        final ValidationContext ctx = context.createContext(path, node);

        return ctx.getValidator(child);
    }

    /**
     * Build the children node validator queue, by walking the elements of
     * the instance node (which is an array, remember) and calling
     * {@link #getValidator(String, JsonNode)} for each successive child node.
     */
    @Override
    protected void buildQueue()
    {
        int i = 0;
        String path;
        Validator v;

        for (final JsonNode child: instance) {
            path = Integer.toString(i++);
            v = getValidator(path, child);
            queue.add(v);
        }
    }
}
