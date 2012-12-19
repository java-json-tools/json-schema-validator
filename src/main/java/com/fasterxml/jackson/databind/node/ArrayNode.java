package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Override of Jackson's {@code ArrayNode}
 *
 * <p>We override it to make it {@code final} (the original implementation
 * isn't), which also allows for code simplification all around.</p>
 */
public final class ArrayNode
    extends ContainerNode<ArrayNode>
{
    private static final Joiner JOINER = Joiner.on(',');

    private final List<JsonNode> elements = Lists.newArrayList();

    public ArrayNode(final JsonNodeFactory nc)
    {
        super(nc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArrayNode deepCopy()
    {
        final ArrayNode ret = new ArrayNode(_nodeFactory);
        for (final JsonNode element: elements)
            ret.elements.add(element.deepCopy());
        return ret;
    }

    /*
    /**********************************************************
    /* Implementation of core JsonNode API
    /**********************************************************
     */

    @Override
    public JsonToken asToken()
    {
        return JsonToken.START_ARRAY;
    }

    @Override
    public boolean isArray()
    {
        return true;
    }

    @Override
    public int size()
    {
        return elements.size();
    }

    @Override
    public Iterator<JsonNode> elements()
    {
        return elements.iterator();
    }

    @Override
    public JsonNode get(final int index)
    {
        if (index < 0)
            return null;
        if (index >= elements.size())
            return null;
        return elements.get(index);
    }

    @Override
    public JsonNode get(final String fieldName)
    {
        return null;
    }

    @Override
    public JsonNode path(final String fieldName)
    {
        return MissingNode.getInstance();
    }

    @Override
    public JsonNode path(final int index)
    {
        if (index < 0)
            return MissingNode.getInstance();
        if (index >= elements.size())
            return MissingNode.getInstance();
        return elements.get(index);
    }
    
    /*
    /**********************************************************
    /* Public API, serialization
    /**********************************************************
     */

    @Override
    public void serialize(final JsonGenerator jgen,
        final SerializerProvider provider)
        throws IOException
    {
        jgen.writeStartArray();
        for (final JsonNode n: elements)
            ((JsonSerializable) n).serialize(jgen, provider);
        jgen.writeEndArray();
    }

    @Override
    public void serializeWithType(final JsonGenerator jgen,
        final SerializerProvider provider, final TypeSerializer typeSer)
        throws IOException
    {
        typeSer.writeTypePrefixForArray(this, jgen);
        for (final JsonNode n: elements)
            ((JsonSerializable) n).serialize(jgen, provider);
        typeSer.writeTypeSuffixForArray(this, jgen);
    }
    
    /*
    /**********************************************************
    /* Public API, finding value nodes
    /**********************************************************
     */

    @Override
    public JsonNode findValue(final String fieldName)
    {
        for (final JsonNode node: elements) {
            final JsonNode value = node.findValue(fieldName);
            if (value != null)
                return value;
        }
        return null;
    }

    @Override
    public List<JsonNode> findValues(final String fieldName,
        List<JsonNode> foundSoFar)
    {
        for (final JsonNode node: elements)
            foundSoFar = node.findValues(fieldName, foundSoFar);
        return foundSoFar;
    }

    @Override
    public List<String> findValuesAsText(final String fieldName,
        List<String> foundSoFar)
    {
        for (final JsonNode node: elements)
            foundSoFar = node.findValuesAsText(fieldName, foundSoFar);
        return foundSoFar;
    }

    @Override
    public ObjectNode findParent(final String fieldName)
    {
        for (final JsonNode node: elements) {
            final JsonNode parent = node.findParent(fieldName);
            if (parent != null)
                return (ObjectNode) parent;
        }
        return null;
    }

    @Override
    public List<JsonNode> findParents(final String fieldName,
        List<JsonNode> foundSoFar)
    {
        for (final JsonNode node: elements)
            foundSoFar = node.findParents(fieldName, foundSoFar);
        return foundSoFar;
    }
    
    /*
    /**********************************************************
    /* Extended ObjectNode API, accessors
    /**********************************************************
     */

    /**
     * Method that will set specified field, replacing old value,
     * if any.
     *
     * @param value to set field to; if null, will be converted
     * to a {@link NullNode} first  (to remove field entry, call
     * {@link #remove} instead)
     * @return Old value of the field, if any; null if there was no
     *         old value.
     */
    public JsonNode set(final int index, final JsonNode value)
    {
        return _set(index, value == null ? nullNode() : value);
    }

    /**
     * Method for adding specified node at the end of this array.
     *
     * @return This node, to allow chaining
     */
    public ArrayNode add(final JsonNode value)
    {
        final JsonNode node = value == null ? nullNode() : value;
        elements.add(node);
        return this;
    }

    /**
     * Method for adding all child nodes of given Array, appending to
     * child nodes this array contains
     *
     * @param other Array to add contents from
     * @return This node (to allow chaining)
     */
    public ArrayNode addAll(final ArrayNode other)
    {
        elements.addAll(other.elements);
        return this;
    }

    /**
     * Method for adding given nodes as child nodes of this array node.
     *
     * @param nodes Nodes to add
     * @return This node (to allow chaining)
     */
    public ArrayNode addAll(final Collection<JsonNode> nodes)
    {
        elements.addAll(nodes);
        return this;
    }

    /**
     * Method for inserting specified child node as an element
     * of this Array. If index is 0 or less, it will be inserted as
     * the first element; if >= size(), appended at the end, and otherwise
     * inserted before existing element in specified index.
     * No exceptions are thrown for any index.
     *
     * @return This node (to allow chaining)
     */
    public ArrayNode insert(final int index, final JsonNode value)
    {
        doInsert(index, value == null ? nullNode() : value);
        return this;
    }

    /**
     * Method for removing an entry from this ArrayNode.
     * Will return value of the entry at specified index, if entry existed;
     * null if not.
     *
     * @return Node removed, if any; null if none
     */
    public JsonNode remove(final int index)
    {
        if (index < 0)
            return null;
        if (index >= elements.size())
            return null;
        return elements.remove(index);
    }

    /**
     * Method for removing all elements of this array, leaving the
     * array empty.
     *
     * @return This node (to allow chaining)
     */
    @Override
    public ArrayNode removeAll()
    {
        elements.clear();
        return this;
    }
    
    /*
    /**********************************************************
    /* Extended ObjectNode API, mutators, generic; addXxx()/insertXxx()
    /**********************************************************
     */

    /**
     * Method that will construct an ArrayNode and add it as a
     * field of this ObjectNode, replacing old value, if any.
     *
     * @return Newly constructed ArrayNode
     */
    public ArrayNode addArray()
    {
        final ArrayNode n = arrayNode();
        elements.add(n);
        return n;
    }

    /**
     * Method that will construct an ObjectNode and add it at the end
     * of this array node.
     *
     * @return Newly constructed ObjectNode
     */
    public ObjectNode addObject()
    {
        final ObjectNode n = objectNode();
        elements.add(n);
        return n;
    }

    /**
     * Method that will construct a POJONode and add it at the end
     * of this array node.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode addPOJO(final Object value)
    {
        elements.add(value == null ? nullNode() : POJONode(value));
        return this;
    }

    /**
     * Method that will add a null value at the end of this array node.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode addNull()
    {
        elements.add(nullNode());
        return this;
    }

    /**
     * Method for adding specified number at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final int v)
    {
        elements.add(numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final Integer value)
    {
        elements.add(value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method for adding specified number at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final long v)
    {
        elements.add(numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final Long value)
    {
        elements.add(value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method for adding specified number at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final float v)
    {
        elements.add(numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final Float value)
    {
        elements.add(value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method for adding specified number at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final double v)
    {
        elements.add(numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final Double value)
    {
        elements.add(value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method for adding specified number at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final BigDecimal v)
    {
        elements.add(v == null ? nullNode() : numberNode(v));
        return this;
    }

    /**
     * Method for adding specified String value at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final String v)
    {
        elements.add(v == null ? nullNode() : textNode(v));
        return this;
    }

    /**
     * Method for adding specified boolean value at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final boolean v)
    {
        elements.add(booleanNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final Boolean value)
    {
        elements.add(value == null ? nullNode() : booleanNode(value));
        return this;
    }

    /**
     * Method for adding specified binary value at the end of this array
     * (note: when serializing as JSON, will be output Base64 encoded)
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(final byte[] v)
    {
        elements.add(v == null ? nullNode() : binaryNode(v));
        return this;
    }

    /**
     * Method for creating an array node, inserting it at the
     * specified point in the array,
     * and returning the <b>newly created array</b>
     * (note: NOT 'this' array)
     */
    public ArrayNode insertArray(final int index)
    {
        final ArrayNode n = arrayNode();
        doInsert(index, n);
        return n;
    }

    /**
     * Method for creating an {@link ObjectNode}, appending it at the end
     * of this array, and returning the <b>newly created node</b>
     * (note: NOT 'this' array)
     *
     * @return Newly constructed ObjectNode
     */
    public ObjectNode insertObject(final int index)
    {
        final ObjectNode n = objectNode();
        doInsert(index, n);
        return n;
    }

    /**
     * Method that will construct a POJONode and
     * insert it at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insertPOJO(final int index, final Object value)
    {
        doInsert(index, value == null ? nullNode() : POJONode(value));
        return this;
    }

    /**
     * Method that will insert a null value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insertNull(final int index)
    {
        doInsert(index, nullNode());
        return this;
    }

    /**
     * Method that will insert specified numeric value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final int v)
    {
        doInsert(index, numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final Integer value)
    {
        doInsert(index, value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method that will insert specified numeric value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final long v)
    {
        doInsert(index, numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final Long value)
    {
        doInsert(index, value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method that will insert specified numeric value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final float v)
    {
        doInsert(index, numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final Float value)
    {
        doInsert(index, value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method that will insert specified numeric value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final double v)
    {
        doInsert(index, numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final Double value)
    {
        doInsert(index, value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method that will insert specified numeric value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final BigDecimal v)
    {
        doInsert(index, v == null ? nullNode() : numberNode(v));
        return this;
    }

    /**
     * Method that will insert specified String
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final String v)
    {
        doInsert(index, v == null ? nullNode() : textNode(v));
        return this;
    }

    /**
     * Method that will insert specified String
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final boolean v)
    {
        doInsert(index, booleanNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final Boolean value)
    {
        doInsert(index, value == null ? nullNode() : booleanNode(value));
        return this;
    }

    /**
     * Method that will insert specified binary value
     * at specified position in this array
     * (note: when written as JSON, will be Base64 encoded)
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(final int index, final byte[] v)
    {
        doInsert(index, v == null ? nullNode() : binaryNode(v));
        return this;
    }

    /*
    /**********************************************************
    /* Standard methods
    /**********************************************************
     */

    @Override
    public boolean equals(final Object o)
    {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != getClass())
            return false;
        return elements.equals(((ArrayNode) o).elements);
    }

    @Override
    public int hashCode()
    {
        return elements.hashCode();
    }


    @Override
    public String toString()
    {
        return '[' + JOINER.join(elements) + ']';
    }

    /*
    /**********************************************************
    /* Internal methods
    /**********************************************************
     */

    private JsonNode _set(final int index, final JsonNode value)
    {
        if (index < 0 || index >= elements.size())
            throw new IndexOutOfBoundsException("Illegal index " + index
                + ", array size " + size());
        return elements.set(index, value);
    }

    private void doInsert(final int index, final JsonNode node)
    {
        if (index < 0)
            elements.add(0, node);
        else if (index >= elements.size())
            elements.add(node);
        else
            elements.add(index, node);
    }
}
