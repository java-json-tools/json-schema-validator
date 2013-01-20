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

/**
 * Enumeration of validation domains
 *
 * <p>This implementation defines three validation domains: ref resolving,
 * syntax validation and instance validation. This happens to be the order in
 * which validation proceeds.</p>
 *
 * @see Message
 */
public enum Domain
{
    /**
     * Ref resolution
     *
     * <p>This can cover ref resolution proper (ie, JSON Reference resolution)
     * but also all associated mechanism, such as JSON Schema retrieval, URI
     * building, etc.</p>
     */
    REF_RESOLVING("$ref resolving"),
    /**
     * Syntax validation
     */
    SYNTAX("syntax"),
    /**
     * Instance validation
     */
    VALIDATION("validation");

    /**
     * The domain as a string
     */
    private final String domain;

    Domain(final String domain)
    {
        this.domain = domain;
    }

    @Override
    public String toString()
    {
        return domain;
    }

    /**
     * Create a {@link Message.Builder} for this validation domain
     *
     * @see Message
     *
     * @return a message builder
     */
    public Message.Builder newMessage()
    {
        return new Message.Builder(this);
    }
}
