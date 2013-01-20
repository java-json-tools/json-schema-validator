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

package com.github.fge.jsonschema.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.github.fge.jsonschema.keyword.KeywordValidator;
import com.github.fge.jsonschema.syntax.SyntaxChecker;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * One validation message
 *
 * <p>A validation message consists of three mandatory fields and optional
 * information. The three mandatory fields are:</p>
 *
 * <ul>
 *     <li>the validation domain (see {@link Domain});</li>
 *     <li>the associated keyword;</li>
 *     <li>the validation message.</li>
 * </ul>
 *
 * <p>Supplementary information can be added via the different {@code
 * .addInfo()} methods.</p>
 *
 * <p>Example of the JSON representation of one message:</p>
 *
 * <pre>
 *     {
 *         "domain": "validation",
 *         "keyword": "maxItems",
 *         "message": "too many elements in array",
 *         "maxItems": 4,
 *         "found": 5
 *     }
 * </pre>
 *
 * <p>You cannot instantiate this class directly: use {@link
 * Domain#newMessage()} for that.</p>
 *
 * <p>This class is immutable.</p>
 *
 * @see Builder
 * @see ValidationReport
 */
public final class Message
    implements Comparable<Message>
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final Joiner JOINER = Joiner.on("; ");

    private final Domain domain;
    private final String keyword;
    private final String message;
    private final boolean fatal;
    private final Map<String, JsonNode> info;

    private Message(final Builder builder)
    {
        domain = builder.domain;
        keyword = builder.keyword;
        message = builder.message;
        fatal = builder.fatal;
        info = ImmutableMap.copyOf(JacksonUtils.asMap(builder.info));
    }

    public Domain getDomain()
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

    public boolean isFatal()
    {
        return fatal;
    }

    public JsonNode toJsonNode()
    {
        final ObjectNode ret = FACTORY.objectNode()
            .put("domain", domain.toString()).put("keyword", keyword)
            .put("message", message);

        if (fatal)
            ret.put("fatal", true);

        ret.putAll(info);
        return ret;
    }

    /**
     * Implementation of {@link Comparable} for this class
     *
     * <p>The compared fields are the three mandatory parameters. Note that
     * whether the message is fatal has no influence here, since if a message is
     * fatal it will be the only reported message.</p>
     *
     * @param o the message to compare to
     * @return the result of the comparison
     */
    @Override
    public int compareTo(final Message o)
    {
        return ComparisonChain.start()
            .compare(domain, o.domain)
            .compare(keyword, o.keyword)
            .compare(message, o.message)
            .result();
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(domain, keyword, message, fatal, info);
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

        final Message other = (Message) obj;

        return domain == other.domain
            && keyword.equals(other.keyword)
            && message.equals(other.message)
            && fatal == other.fatal
            && info.equals(other.info);
    }

    @Override
    public String toString()
    {
        final List<String> list = new ArrayList<String>();

        list.add("domain: " + domain.toString());
        list.add("keyword: " + keyword);
        list.add("message: " + message);

        for (final String key: Ordering.natural().sortedCopy(info.keySet()))
            list.add(key + ": " + info.get(key));

        return (fatal ? "FATAL ERROR: " : "") + JOINER.join(list);
    }

    /**
     * Builder class for a {@link Message}
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
            "keyword", "message", "fatal");

        /**
         * Validation domain
         */
        private final Domain domain;

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
        private final ObjectNode info = FACTORY.objectNode();

        /**
         * Is this error message fatal?
         */
        private boolean fatal = false;

        Builder(final Domain domain)
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
         * Should this error message be marked as fatal?
         *
         * @param fatal true if this error message is fatal (false by default)
         * @return the builder
         */
        public Builder setFatal(final boolean fatal)
        {
            this.fatal = fatal;
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
            final ArrayNode node = FACTORY.arrayNode();

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
         * @return a {@link Message}
         * @throws NullPointerException the keyword or message are null
         */
        public Message build()
        {
            Preconditions.checkNotNull(keyword, "keyword is null");
            Preconditions.checkNotNull(message, "message is null");
            info.remove(RESERVED);

            return new Message(this);
        }
    }
}
