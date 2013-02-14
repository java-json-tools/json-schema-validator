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

package com.github.fge.jsonschema.main;

import com.github.fge.jsonschema.cfg.LoadingConfiguration;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.report.ReportProvider;
import com.github.fge.jsonschema.util.Frozen;

public final class JsonSchemaFactory
    implements Frozen<JsonSchemaFactoryBuilder>
{
    final ReportProvider reportProvider;
    final LoadingConfiguration loadingConfiguration;
    final ValidationConfiguration validationConfiguration;

    public static JsonSchemaFactory byDefault()
    {
        return newBuilder().freeze();
    }

    public static JsonSchemaFactoryBuilder newBuilder()
    {
        return new JsonSchemaFactoryBuilder();
    }

    JsonSchemaFactory(final JsonSchemaFactoryBuilder builder)
    {
        reportProvider = builder.reportProvider;
        loadingConfiguration = builder.loadingConfiguration;
        validationConfiguration = builder.validationConfiguration;
    }

    public JsonValidator getValidator()
    {
        return new JsonValidator(this);
    }

    @Override
    public JsonSchemaFactoryBuilder thaw()
    {
        return new JsonSchemaFactoryBuilder(this);
    }
}
