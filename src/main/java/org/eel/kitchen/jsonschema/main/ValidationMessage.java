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

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ValidationMessage
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;
    private final ValidationDomain domain;
    private final String keyword;
    private final String message;
    private final Map<String, JsonNode> info;

    private ValidationMessage(final Builder builder)
    {
        domain = builder.domain;
        keyword = builder.keyword;
        message = builder.message;
        info = ImmutableMap.copyOf(builder.info);
    }

    public static Builder defaultBuilder()
    {
        return new Builder(ValidationDomain.UNKNOWN).setKeyword("(not set)");
    }

    public JsonNode toJsonNode()
    {
        final ObjectNode ret = factory.objectNode()
            .put("domain", domain.toString()).put("keyword", keyword)
            .put("message", message);

        ret.putAll(info);
        return ret;
    }

    public String getMessage()
    {
        return message;
    }

    public static final class Builder
    {
        private static final Set<String> RESERVED = ImmutableSet.of("domain",
            "keyword", "message");

        private final ValidationDomain domain;
        private String keyword;
        private String message;
        private final Map<String, JsonNode> info
            = new HashMap<String, JsonNode>();

        public Builder(final ValidationDomain domain)
        {
            Preconditions.checkNotNull(domain, "domain is null");
            this.domain = domain;
        }

        public Builder setKeyword(final String keyword)
        {
            this.keyword = keyword;
            return this;
        }

        public Builder setMessage(final String message)
        {
            this.message = message;
            return this;
        }

        public Builder info(final String key, final JsonNode value)
        {
            info.put(key, value);
            return this;
        }

        public Builder clearInfo()
        {
            info.clear();
            return this;
        }

        public ValidationMessage build()
        {
            Preconditions.checkNotNull(keyword, "keyword is null");
            Preconditions.checkNotNull(message, "message is null");
            for (final String reserved: RESERVED)
                info.remove(reserved);

            return new ValidationMessage(this);
        }
    }
}
