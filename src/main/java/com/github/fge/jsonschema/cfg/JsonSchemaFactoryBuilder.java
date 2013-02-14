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

package com.github.fge.jsonschema.cfg;

import com.github.fge.jsonschema.exceptions.unchecked.FactoryConfigurationError;
import com.github.fge.jsonschema.report.ListReportProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ReportProvider;
import com.github.fge.jsonschema.util.Thawed;

import static com.github.fge.jsonschema.messages.ConfigurationMessages.*;

public final class JsonSchemaFactoryBuilder
    implements Thawed<JsonSchemaFactory>
{
    ReportProvider reportProvider;
    LoadingConfiguration loadingConfiguration;
    ValidationConfiguration validationConfiguration;

    JsonSchemaFactoryBuilder()
    {
        reportProvider = ListReportProvider.getInstance();
        loadingConfiguration = LoadingConfiguration.byDefault();
        validationConfiguration = ValidationConfiguration.byDefault();
    }

    JsonSchemaFactoryBuilder(final JsonSchemaFactory factory)
    {
        reportProvider = factory.reportProvider;
    }

    public JsonSchemaFactoryBuilder setReportProvider(
        final ReportProvider reportProvider)
    {
        if (reportProvider == null)
            throw new FactoryConfigurationError(new ProcessingMessage()
                .message(NULL_REPORT_PROVIDER));
        this.reportProvider = reportProvider;
        return this;
    }

    public void setLoadingConfiguration(
        final LoadingConfiguration loadingConfiguration)
    {
        if (loadingConfiguration == null)
            throw new FactoryConfigurationError(new ProcessingMessage()
                .message(NULL_LOADING_CFG));
        this.loadingConfiguration = loadingConfiguration;
    }

    public void setValidationConfiguration(
        final ValidationConfiguration validationConfiguration)
    {
        if (validationConfiguration == null)
            throw new FactoryConfigurationError(new ProcessingMessage()
                .message(NULL_VALIDATION_CFG));
        this.validationConfiguration = validationConfiguration;
    }

    @Override
    public JsonSchemaFactory freeze()
    {
        return new JsonSchemaFactory(this);
    }
}
