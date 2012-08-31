/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.JacksonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * One validation message
 *
 * <p>A validation message consists of three mandatory fields and optional
 * information. The three mandatory fields are:</p>
 *
 * <ul>
 *     <li>the validation domain (see {@link ValidationDomain});</li>
 *     <li>the associated keyword;</li>
 *     <li>the validation message.</li>
 * </ul>
 *
 * <p>You cannot instantiate this class directly: you need to use
 * {@link Builder} for that.</p>
 *
 * <p>This class is immutable.</p>
 *
 * @see Builder
 * @see ValidationReport
 */
public final class ValidationMessage
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;
    private static final Joiner JOINER = Joiner.on("; ");

    private final ValidationDomain domain;
    private final String keyword;
    private final String message;
    private final Map<String, JsonNode> info;

    private ValidationMessage(final Builder builder)
    {
        domain = builder.domain;
        keyword = builder.keyword;
        message = builder.message;
        info = ImmutableMap.copyOf(JacksonUtils.nodeToMap(builder.info));
    }

    public ValidationDomain getDomain()
    {
        return domain;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public String getMessage()
    {
        return message;
    }

    public JsonNode getInfo(final String key)
    {
        return info.get(key).deepCopy();
    }

    public JsonNode toJsonNode()
    {
        final ObjectNode ret = factory.objectNode()
            .put("domain", domain.toString()).put("keyword", keyword)
            .put("message", message);

        ret.putAll(info);
        return ret;
    }

    @Override
    public int hashCode()
    {
        int ret = domain.hashCode();
        ret *= 31;
        ret += keyword.hashCode();
        ret *= 31;
        ret += message.hashCode();
        ret *= 31;
        ret += info.hashCode();
        return ret;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;

        if (getClass() != obj.getClass())
            return false;

        final ValidationMessage other = (ValidationMessage) obj;

        return domain == other.domain
            && keyword.equals(other.keyword)
            && message.equals(other.message)
            && info.equals(other.info);
    }

    @Override
    public String toString()
    {
        final List<String> list = new ArrayList<String>();

        list.add("domain: " + domain.toString());
        list.add("keyword: " + keyword);
        list.add("message: " + message);

        final SortedSet<String> infoKeys = new TreeSet<String>(info.keySet());

        for (final String key: infoKeys)
            list.add(key + ": " + info.get(key));

        return JOINER.join(list);
    }

    /**
     * Builder class for a {@link ValidationMessage}
     *
     * <p>To build a validation message, you instantiate this class, fill the
     * necessary information and finally call {@link #build()} to obtain the
     * message.</p>
     *
     * <p>In most cases, you won't need to instantiate one directly:</p>
     *
     * <ul>
     *     <li>when implementing a {@link SyntaxChecker}, an instance of this
     *     class will be passed as an argument, already filled with the
     *     correct domain and keyword;</li>
     *     <li>when implementing a {@link KeywordValidator}, you can obtain an
     *     instance also filled with the correct domain and keyword.</li>
     * </ul>
     *
     */
    public static final class Builder
    {
        /**
         * Reserved set of keywords which will be removed off {@link #info}
         * before the message is built
         */
        private static final Set<String> RESERVED = ImmutableSet.of("domain",
            "keyword", "message");

        /**
         * Validation domain
         */
        private final ValidationDomain domain;

        /**
         * Keyword associated with the message
         *
         * <p>In some events, it may be set to {@code N/A}.</p>
         */
        private String keyword;

        /**
         * Error message
         */
        private String message;

        /**
         * Further information associated with the error message
         */
        private final ObjectNode info = factory.objectNode();

        /**
         * Constructor
         *
         * @param domain the validation domain
         */
        public Builder(final ValidationDomain domain)
        {
            Preconditions.checkNotNull(domain, "domain is null");
            this.domain = domain;
        }

        /**
         * Set the keyword associated with this message
         *
         * @param keyword the keyword
         * @return the builder
         */
        public Builder setKeyword(final String keyword)
        {
            this.keyword = keyword;
            return this;
        }

        /**
         * Set the error message
         *
         * @param message the error message
         * @return the builder
         */
        public Builder setMessage(final String message)
        {
            this.message = message;
            return this;
        }

        /**
         * Add further information to the message as a {@link JsonNode}
         *
         * @param key the key
         * @param value the value
         * @return the builder
         */
        public Builder addInfo(final String key, final JsonNode value)
        {
            info.put(key, value);
            return this;
        }

        /**
         * Add further information to the message for an arbitrary type
         *
         * <p>This will call {@link Object#toString()} on the passed value. It
         * is therefore important that objects passed as arguments implement it
         * correctly.</p>
         *
         * @param key the key
         * @param <T> the type of the value
         * @param value the value
         * @return the builder
         */
        public <T> Builder addInfo(final String key, final T value)
        {
            info.put(key, value.toString());
            return this;
        }

        /**
         * Add further information to the message as a {@link Collection} of
         * objects of an arbitrary type
         *
         * <p>This will call {@link Object#toString()} on each element of the
         * collection.It is therefore important that objects passed as arguments
         * implement it correctly.</p>
         *
         * @see #addInfo(String, Object)
         *
         * @param key the key
         * @param <T> the type of values in the collections
         * @param values the collection
         * @return the builder
         */
        public <T> Builder addInfo(final String key, final Collection<T> values)
        {
            final ArrayNode node = factory.arrayNode();

            for (final T value: values)
                node.add(value.toString());

            info.put(key, node);
            return this;
        }

        /**
         * Add further information to the message as an integer
         *
         * @param key the key
         * @param value the value
         * @return the builder
         */
        public Builder addInfo(final String key, final int value)
        {
            info.put(key, value);
            return this;
        }

        /**
         * Clear all supplementary information
         *
         * <p>This <b>will not</b> reset the domain, keyword or message.</p>
         *
         * @return the builder
         */
        public Builder clearInfo()
        {
            info.removeAll();
            return this;
        }

        /**
         * Build the actual message
         *
         * @return a {@link ValidationMessage}
         * @throws NullPointerException the keyword or message are null
         */
        public ValidationMessage build()
        {
            Preconditions.checkNotNull(keyword, "keyword is null");
            Preconditions.checkNotNull(message, "message is null");
            info.remove(RESERVED);

            return new ValidationMessage(this);
        }
    }
}
