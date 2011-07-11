package eel.kitchen.util;

import org.codehaus.jackson.JsonNode;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>"Decorate" a {@link JsonNode} by building a Map&lt;String, JsonNode&gt;
 * so that it implement {@link Iterable&lt;String, JsonNode&gt;} to navigate
 * more easily into its entries.</p>
 *
 * <p>For object nodes, JsonNode has .getFields(), which returns an appropriate
 * {@link Iterator}, and we use that to build our map. For other types, we take
 * advantage of the fact that JsonNode itself implements
 * Iterable&lt;JsonNode&gt;, and that the iterator is non empty only for arrays.
 * In the case of an array, the keys are <code>[x]</code>, where <code>x</code>
 * is the index within the array. We guarantee the order by using a
 * {@link LinkedHashMap} internally. The internal map is read-protected by using
 * {@link Collections}' * .unmodifiableMap() method.
 * </p>
 */

public final class IterableJsonNode
    implements Iterable<Map.Entry<String, JsonNode>>
{
    private final Map<String, JsonNode> map;

    /**
     * Constructor. Note that despite the fact that {@link JsonNode} has
     * .getFields(), we don't use that as the result of .iterator(): we want
     * the result to be read only.
     *
     * @param node The JsonNode instance to iterate over
     */

    public IterableJsonNode(final JsonNode node)
    {
        final Map<String, JsonNode> tmp = new LinkedHashMap<String, JsonNode>();

        if (node.isObject())
            tmp.putAll(CollectionUtils.toMap(node.getFields()));
        else {
            int i = 0;
            for (final JsonNode element: node)
                tmp.put(String.format("[%d]", i++), element);
        }

        map = Collections.unmodifiableMap(tmp);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public Iterator<Map.Entry<String, JsonNode>> iterator()
    {
        return map.entrySet().iterator();
    }
}
