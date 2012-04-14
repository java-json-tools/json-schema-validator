/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.bundle.DraftV3ValidatorBundle;
import org.eel.kitchen.jsonschema.bundle.DraftV4ValidatorBundle;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of JSON Schema versions
 *
 * <p>While currently only draft v3 really exists, we also define draft v4.
 * This enum holds two elements:</p>
 * <ul>
 *     <li>the URI of this schema as a {@link String},</li>
 *     <li>the matching {@link ValidatorBundle}.</li>
 * </ul>
 */
public enum SchemaVersion
{
    DRAFT_V3("http://json-schema.org/draft-03/schema#",
        DraftV3ValidatorBundle.getInstance()),
    DRAFT_V4("http://json-schema.org/draft-04/schema#",
        DraftV4ValidatorBundle.getInstance());

    /**
     * Reverse map of locators to versions
     *
     * @see #getVersion(JsonNode)
     */
    private static final Map<String, SchemaVersion> locatorMap
        = new HashMap<String, SchemaVersion>();

    /**
     * This schema version's locator
     */
    private final String locator;

    /**
     * This schema version's base validator bundle
     */
    private final ValidatorBundle bundle;

    static {
        for (final SchemaVersion version: values())
            locatorMap.put(version.locator, version);
    }

    SchemaVersion(final String locator, final ValidatorBundle bundle)
    {
        this.locator = locator;
        this.bundle = bundle;
    }

    /**
     * Get the version of a given schema by looking at its {@code $schema}
     * attribute
     *
     * @param schema the schema
     * @return the version, or null if {@code $schema} was not found or its
     * value is unknown
     * @throws JsonValidationFailureException {@code $schema} exists but is
     * not a text node
     */
    public static SchemaVersion getVersion(final JsonNode schema)
        throws JsonValidationFailureException
    {
        final JsonNode node = schema.get("$schema");

        if (node == null)
            return null;

        if (!node.isTextual())
            throw new JsonValidationFailureException("$schema is not a text "
                + "node");

        return locatorMap.get(node.textValue());
    }

    /**
     * Return the validator bundle for this schema version
     *
     * @return a {@link ValidatorBundle}
     */
    public ValidatorBundle getBundle()
    {
        return bundle;
    }
}
