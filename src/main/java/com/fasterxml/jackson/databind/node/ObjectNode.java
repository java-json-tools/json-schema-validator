package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Override of Jackson's {@code ObjectNode}
 *
 * <p>We override it to make it {@code final} (the original implementation
 * isn't), which also allows for code simplification all around.</p>
 */
public final class ObjectNode
    extends ContainerNode<ObjectNode>
{
    private static final Joiner JOINER = Joiner.on(',');

    private final Map<String, JsonNode> members = Maps.newHashMap();

    public ObjectNode(final JsonNodeFactory nc)
    {
        super(nc);
    }


    @SuppressWarnings("unchecked")
    @Override
    public ObjectNode deepCopy()
    {
        final ObjectNode ret = new ObjectNode(_nodeFactory);
        for (final Map.Entry<String, JsonNode> entry: members.entrySet())
            ret.members.put(entry.getKey(), entry.getValue().deepCopy());
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
        return JsonToken.START_OBJECT;
    }

    @Override
    public boolean isObject()
    {
        return true;
    }

    @Override
    public int size()
    {
        return members.size();
    }

    @Override
    public Iterator<JsonNode> elements()
    {
        return members.values().iterator();
    }

    @Override
    public JsonNode get(final int index)
    {
        return null;
    }

    @Override
    public JsonNode get(final String fieldName)
    {
        return members.get(fieldName);
    }

    @Override
    public Iterator<String> fieldNames()
    {
        return members.keySet().iterator();
    }

    @Override
    public JsonNode path(final int index)
    {
        return MissingNode.getInstance();
    }

    @Override
    public JsonNode path(final String fieldName)
    {
        final JsonNode ret = members.get(fieldName);
        return ret == null ? MissingNode.getInstance() : ret;
    }

    /**
     * Method to use for accessing all fields (with both names
     * and values) of this JSON Object.
     */
    @Override
    public Iterator<Map.Entry<String, JsonNode>> fields()
    {
        return members.entrySet().iterator();
    }

    @Override
    public ObjectNode with(final String propertyName)
    {
        final JsonNode n = members.get(propertyName);
        if (n != null) {
            if (n instanceof ObjectNode)
                return (ObjectNode) n;
            throw new UnsupportedOperationException("Property '" + propertyName
                + "' has value that is not of type ObjectNode (but " + n
                .getClass().getName() + ")");
        }
        final ObjectNode result = objectNode();
        members.put(propertyName, result);
        return result;
    }

    @Override
    public ArrayNode withArray(final String propertyName)
    {
        final JsonNode n = members.get(propertyName);
        if (n != null) {
            if (n instanceof ArrayNode)
                return (ArrayNode) n;
            throw new UnsupportedOperationException("Property '" + propertyName
                + "' has value that is not of type ArrayNode (but " + n
                .getClass().getName() + ")");
        }
        final ArrayNode result = arrayNode();
        members.put(propertyName, result);
        return result;
    }
    
    /*
    /**********************************************************
    /* Public API, finding value nodes
    /**********************************************************
     */

    @Override
    public JsonNode findValue(final String fieldName)
    {
        for (final Map.Entry<String, JsonNode> entry: members.entrySet()) {
            if (fieldName.equals(entry.getKey()))
                return entry.getValue();
            final JsonNode value = entry.getValue().findValue(fieldName);
            if (value != null)
                return value;
        }
        return null;
    }

    @Override
    public List<JsonNode> findValues(final String fieldName,
        List<JsonNode> foundSoFar)
    {
        for (final Map.Entry<String, JsonNode> entry: members.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null)
                    foundSoFar = new ArrayList<JsonNode>();
                foundSoFar.add(entry.getValue());
            } else
                foundSoFar = entry.getValue().findValues(fieldName, foundSoFar);
        }
        return foundSoFar;
    }

    @Override
    public List<String> findValuesAsText(final String fieldName,
        List<String> foundSoFar)
    {
        for (final Map.Entry<String, JsonNode> entry: members.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null)
                    foundSoFar = new ArrayList<String>();
                foundSoFar.add(entry.getValue().asText());
            } else
                foundSoFar = entry.getValue()
                    .findValuesAsText(fieldName, foundSoFar);
        }
        return foundSoFar;
    }

    @Override
    public ObjectNode findParent(final String fieldName)
    {
        for (final Map.Entry<String, JsonNode> entry: members.entrySet()) {
            if (fieldName.equals(entry.getKey()))
                return this;
            final JsonNode value = entry.getValue().findParent(fieldName);
            if (value != null)
                return (ObjectNode) value;
        }
        return null;
    }

    @Override
    public List<JsonNode> findParents(final String fieldName,
        List<JsonNode> foundSoFar)
    {
        for (final Map.Entry<String, JsonNode> entry : members.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null)
                    foundSoFar = new ArrayList<JsonNode>();
                foundSoFar.add(this);
            } else
                foundSoFar = entry.getValue()
                    .findParents(fieldName, foundSoFar);
        }
        return foundSoFar;
    }
    
    /*
    /**********************************************************
    /* Public API, serialization
    /**********************************************************
     */

    /**
     * Method that can be called to serialize this node and
     * all of its descendants using specified JSON generator.
     */
    @Override
    public void serialize(final JsonGenerator jgen,
        final SerializerProvider provider)
        throws IOException
    {
        jgen.writeStartObject();
        for (final Map.Entry<String, JsonNode> en: members.entrySet()) {
            jgen.writeFieldName(en.getKey());
                /* 17-Feb-2009, tatu: Can we trust that all nodes will always
                 *   extend BaseJsonNode? Or if not, at least implement
                 *   JsonSerializable? Let's start with former, change if
                 *   we must.
                 */
            ((JsonSerializable) en.getValue()).serialize(jgen, provider);
        }
        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(final JsonGenerator jgen,
        final SerializerProvider provider, final TypeSerializer typeSer)
        throws IOException
    {
        typeSer.writeTypePrefixForObject(this, jgen);
        for (final Map.Entry<String, JsonNode> en: members.entrySet()) {
            jgen.writeFieldName(en.getKey());
            ((JsonSerializable) en.getValue()).serialize(jgen, provider);
        }
        typeSer.writeTypeSuffixForObject(this, jgen);
    }

    /*
    /**********************************************************
    /* Extended ObjectNode API, mutators, since 2.1
    /**********************************************************
     */

    /**
     * Method that will set specified field, replacing old value, if any.
     * Note that this is identical to {@link #replace(String, JsonNode)},
     * except for return value.
     * <p>
     * NOTE: added to replace those uses of {@link #put(String, JsonNode)}
     * where chaining with 'this' is desired.
     *
     * @param value to set field to; if null, will be converted
     * to a {@link NullNode} first  (to remove field entry, call
     * {@link #remove} instead)
     * @return This node after adding/replacing property value (to allow chaining)
     * @since 2.1
     */
    public JsonNode set(final String fieldName, final JsonNode value)
    {
        members.put(fieldName, value == null ? nullNode() : value);
        return this;
    }

    /**
     * Method for adding given properties to this object node, overriding
     * any existing values for those properties.
     *
     * @param properties Properties to add
     * @return This node after adding/replacing property values (to allow chaining)
     * @since 2.1
     */
    public JsonNode setAll(final Map<String, JsonNode> properties)
    {
        JsonNode node;
        for (final Map.Entry<String, JsonNode> en: properties.entrySet()) {
            node = en.getValue();
            if (node == null)
                node = nullNode();
            members.put(en.getKey(), node);
        }
        return this;
    }

    /**
     * Method for adding all properties of the given Object, overriding
     * any existing values for those properties.
     *
     * @param other Object of which properties to add to this object
     * @return This node after addition (to allow chaining)
     * @since 2.1
     */
    public JsonNode setAll(final ObjectNode other)
    {
        members.putAll(other.members);
        return this;
    }

    /**
     * Method for replacing value of specific property with passed
     * value, and returning value (or null if none).
     *
     * @param fieldName Property of which value to replace
     * @param value Value to set property to, replacing old value if any
     * @return Old value of the property; null if there was no such property
     *         with value
     * @since 2.1
     */
    public JsonNode replace(final String fieldName, final JsonNode value)
    {
        return members.put(fieldName, value == null ? nullNode() : value);
    }

    /**
     * Method for removing field entry from this ObjectNode, and
     * returning instance after removal.
     *
     * @return This node after removing entry (if any)
     * @since 2.1
     */
    public JsonNode without(final String fieldName)
    {
        members.remove(fieldName);
        return this;
    }

    /**
     * Method for removing specified field properties out of
     * this ObjectNode.
     *
     * @param fieldNames Names of fields to remove
     * @return This node after removing entries
     * @since 2.1
     */
    public ObjectNode without(final Collection<String> fieldNames)
    {
        members.keySet().removeAll(fieldNames);
        return this;
    }
    
    /*
    /**********************************************************
    /* Extended ObjectNode API, mutators, generic
    /**********************************************************
     */

    /**
     * Method that will set specified field, replacing old value, if any.
     *
     * @param value to set field to; if null, will be converted
     * to a {@link NullNode} first  (to remove field entry, call
     * {@link #remove} instead)
     * <p>
     * NOTE: this method will be <b>deprecated</b> in 2.2; and should
     * be replace with either
     * {@link #set(String, JsonNode)} or {@link #replace(String, JsonNode)},
     * depending on which return value is desired for possible chaining.
     * @return Old value of the field, if any; null if there was no
     *         old value.
     */
    public JsonNode put(final String fieldName, final JsonNode value)
    {
        return members.put(fieldName, value == null ? nullNode() : value);
    }

    /**
     * Method for removing field entry from this ObjectNode.
     * Will return value of the field, if such field existed;
     * null if not.
     *
     * @return Value of specified field, if it existed; null if not
     */
    public JsonNode remove(final String fieldName)
    {
        return members.remove(fieldName);
    }

    /**
     * Method for removing specified field properties out of
     * this ObjectNode.
     *
     * @param fieldNames Names of fields to remove
     * @return This node after removing entries
     */
    public ObjectNode remove(final Collection<String> fieldNames)
    {
        members.keySet().removeAll(fieldNames);
        return this;
    }

    /**
     * Method for removing all field properties, such that this
     * ObjectNode will contain no properties after call.
     *
     * @return This node after removing all entries
     */
    @Override
    public ObjectNode removeAll()
    {
        members.clear();
        return this;
    }

    /**
     * Method for adding given properties to this object node, overriding
     * any existing values for those properties.
     * <p>
     * NOTE: this method will be <b>deprecated</b> in 2.2; and should
     * be replace with {@link #setAll(Map)}.
     *
     * @param properties Properties to add
     * @return This node after adding/replacing property values (to allow chaining)
     */
    public JsonNode putAll(final Map<String, JsonNode> properties)
    {
        return setAll(properties);
    }

    /**
     * Method for adding all properties of the given Object, overriding
     * any existing values for those properties.
     * <p>
     * NOTE: this method will be <b>deprecated</b> in 2.2; and should
     * be replace with {@link #setAll(ObjectNode)}.
     *
     * @param other Object of which properties to add to this object
     * @return This node (to allow chaining)
     */
    public JsonNode putAll(final ObjectNode other)
    {
        return setAll(other);
    }

    /**
     * Method for removing all field properties out of this ObjectNode
     * <b>except</b> for ones specified in argument.
     *
     * @param fieldNames Fields to <b>retain</b> in this ObjectNode
     * @return This node (to allow call chaining)
     */
    public ObjectNode retain(final Collection<String> fieldNames)
    {
        members.keySet().retainAll(fieldNames);
        return this;
    }

    /**
     * Method for removing all field properties out of this ObjectNode
     * <b>except</b> for ones specified in argument.
     *
     * @param fieldNames Fields to <b>retain</b> in this ObjectNode
     * @return This node (to allow call chaining)
     */
    public ObjectNode retain(final String... fieldNames)
    {
        return retain(Arrays.asList(fieldNames));
    }
    
    /*
    /**********************************************************
    /* Extended ObjectNode API, mutators, typed
    /**********************************************************
     */

    /**
     * Method that will construct an ArrayNode and add it as a
     * field of this ObjectNode, replacing old value, if any.
     * <p>
     * <b>NOTE</b>: Unlike all <b>put(...)</b> methods, return value
     * is <b>NOT</b> this <code>ObjectNode</code>, but the
     * <b>newly created</b> <code>ArrayNode</code> instance.
     *
     * @return Newly constructed ArrayNode (NOT the old value,
     *         which could be of any type)
     */
    public ArrayNode putArray(final String fieldName)
    {
        final ArrayNode n = arrayNode();
        members.put(fieldName, n);
        return n;
    }

    /**
     * Method that will construct an ObjectNode and add it as a
     * field of this ObjectNode, replacing old value, if any.
     * <p>
     * <b>NOTE</b>: Unlike all <b>put(...)</b> methods, return value
     * is <b>NOT</b> this <code>ObjectNode</code>, but the
     * <b>newly created</b> <code>ObjectNode</code> instance.
     *
     * @return Newly constructed ObjectNode (NOT the old value,
     *         which could be of any type)
     */
    public ObjectNode putObject(final String fieldName)
    {
        final ObjectNode n = objectNode();
        members.put(fieldName, n);
        return n;
    }

    /**
     * @return This node (to allow chaining)
     */
    public ObjectNode putPOJO(final String fieldName, final Object pojo)
    {
        members.put(fieldName, POJONode(pojo));
        return this;
    }

    /**
     * @return This node (to allow chaining)
     */
    public ObjectNode putNull(final String fieldName)
    {
        members.put(fieldName, nullNode());
        return this;
    }

    /**
     * Method for setting value of a field to specified numeric value.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final int v)
    {
        members.put(fieldName, numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final Integer value)
    {
        members.put(fieldName, value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method for setting value of a field to specified numeric value.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final long v)
    {
        members.put(fieldName, numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final Long value)
    {
        members.put(fieldName, value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method for setting value of a field to specified numeric value.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final float v)
    {
        members.put(fieldName, numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final Float value)
    {
        members.put(fieldName, value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method for setting value of a field to specified numeric value.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final double v)
    {
        members.put(fieldName, numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final Double value)
    {
        members.put(fieldName, value == null ? nullNode() : numberNode(value));
        return this;
    }

    /**
     * Method for setting value of a field to specified numeric value.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final BigDecimal v)
    {
        members.put(fieldName, v == null ? nullNode() : numberNode(v));
        return this;
    }

    /**
     * Method for setting value of a field to specified String value.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final String v)
    {
        members.put(fieldName, v == null ? nullNode() : textNode(v));
        return this;
    }

    /**
     * Method for setting value of a field to specified String value.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final boolean v)
    {
        members.put(fieldName, booleanNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final Boolean value)
    {
        members.put(fieldName, value == null ? nullNode() : booleanNode(value));
        return this;
    }

    /**
     * Method for setting value of a field to specified binary value
     *
     * @return This node (to allow chaining)
     */
    public ObjectNode put(final String fieldName, final byte[] v)
    {
        members.put(fieldName, v == null ? nullNode() : binaryNode(v));
        return this;
    }

    /*
    /**********************************************************
    /* Overridable methods
    /**********************************************************
     */

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
        final ObjectNode other = (ObjectNode) o;
        return members.equals(other.members);
    }

    @Override
    public int hashCode()
    {
        return members.hashCode();
    }

    @Override
    public String toString()
    {
        final Set<String> set = Sets.newHashSetWithExpectedSize(members.size());

        for (final Map.Entry<String, JsonNode> entry: members.entrySet())
            set.add(entryToString(entry));

        return '{' + JOINER.join(set) + '}';
    }

    private static String entryToString(final Map.Entry<String, JsonNode> entry)
    {
        final StringBuilder sb = new StringBuilder();
        TextNode.appendQuoted(sb, entry.getKey());
        return sb.append(':').append(entry.getValue()).toString();
    }
}
