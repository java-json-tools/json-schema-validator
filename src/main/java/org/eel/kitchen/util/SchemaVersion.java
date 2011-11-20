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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.util;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.bundle.DraftV3ValidatorBundle;
import org.eel.kitchen.jsonschema.bundle.DraftV4ValidatorBundle;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;

import java.util.HashMap;
import java.util.Map;

public enum SchemaVersion
{
    DRAFT_V3("http://json-schema.org/draft-03/schema#",
        new DraftV3ValidatorBundle()),
    DRAFT_V4("http://json-schema.org/draft-04/schema#",
        new DraftV4ValidatorBundle());

    private static final SchemaVersion DEFAULT_VERSION = DRAFT_V3;

    private static final Map<String, SchemaVersion> locatorMap
        = new HashMap<String, SchemaVersion>();

    private final String locator;

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

    public static SchemaVersion getVersion(final JsonNode schema)
        throws JsonValidationFailureException
    {
        if (schema == null)
            throw new JsonValidationFailureException("schema is null");

        if (!schema.isObject())
            throw new JsonValidationFailureException("not a schema (not an "
                + "object)");

        if (!schema.has("$schema"))
            return DEFAULT_VERSION;

        final String s = schema.get("$schema").getTextValue();

        return locatorMap.containsKey(s) ? locatorMap.get(s) : DEFAULT_VERSION;
    }

    public ValidatorBundle getBundle()
    {
        return bundle;
    }
}
