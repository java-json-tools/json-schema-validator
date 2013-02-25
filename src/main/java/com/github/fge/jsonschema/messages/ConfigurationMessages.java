/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.messages;

/**
 * Messages used by the configuration process
 */
//TODO: reorganize
public enum ConfigurationMessages
{
    NULL_SCHEME("scheme cannot be null"),
    EMPTY_SCHEME("cannot register empty scheme"),
    ILLEGAL_SCHEME("illegal scheme"),
    NULL_DEREFERENCING_MODE("dereferencing mode cannot be null"),
    NULL_URI("provided URI cannot be null"),
    INVALID_URI("input is not a valid URI"),
    REF_NOT_ABSOLUTE("input is not an absolute JSON Reference"),
    REDIRECT_TO_SELF("attempt to redirect to self"),
    NULL_SCHEMA("cannot register null schema"),
    DUPLICATE_URI("there is already a schema at that URI"),
    NO_ID_IN_SCHEMA("schema has no id"),
    NULL_REPORT_PROVIDER("report provider must not be null"),
    NULL_LOADING_CFG("loading configuration must not be null"),
    NULL_VALIDATION_CFG("validation configuration must not be null"),
    NULL_LIBRARY("library cannot be null"),
    DUP_LIBRARY("a library already exists for this URI"),
    NULL_VERSION("version cannot be null"),
    NULL_INSTANCE("instance cannot be null"),
    ;

    private final String message;

    ConfigurationMessages(final String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message;
    }
}
