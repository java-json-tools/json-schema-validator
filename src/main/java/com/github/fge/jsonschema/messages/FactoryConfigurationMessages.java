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

import com.github.fge.jsonschema.exceptions.unchecked.FactoryConfigurationError;
import com.github.fge.jsonschema.report.ProcessingMessage;

/**
 * Messages used by the configuration process
 */
//TODO: reorganize
public enum FactoryConfigurationMessages
{
    NULL_REPORT_PROVIDER("report provider must not be null"),
    NULL_LOADING_CFG("loading configuration must not be null"),
    NULL_VALIDATION_CFG("validation configuration must not be null"),
    ;

    private final String message;

    FactoryConfigurationMessages(final String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message;
    }

    public void checkThat(final boolean condition)
    {
        if (!condition)
            throw new FactoryConfigurationError(new ProcessingMessage()
                .message(this));
    }
}
