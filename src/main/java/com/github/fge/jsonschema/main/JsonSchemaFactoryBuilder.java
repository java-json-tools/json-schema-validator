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
import com.github.fge.jsonschema.exceptions.unchecked.FactoryConfigurationError;
import com.github.fge.jsonschema.report.ListReportProvider;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ReportProvider;
import com.github.fge.jsonschema.util.Thawed;
import net.jcip.annotations.NotThreadSafe;

import static com.github.fge.jsonschema.messages.ConfigurationMessages.*;

/**
 * Thawed instance of a {@link JsonSchemaFactory}
 *
 * <p>This is the class you will use to configure a schema factory before use,
 * should you need to. In most cases, the default factory will be enough.</p>
 *
 * <p>In order to obtain an instance of this builder class, use {@link
 * JsonSchemaFactory#newBuilder()}.</p>
 *
 * @see JsonSchemaFactory#byDefault()
 * @see LoadingConfiguration
 * @see ValidationConfiguration
 * @see ReportProvider
 */
@NotThreadSafe
public final class JsonSchemaFactoryBuilder
    implements Thawed<JsonSchemaFactory>
{
    ReportProvider reportProvider;
    LoadingConfiguration loadingCfg;
    ValidationConfiguration validationCfg;

    /**
     * A builder with the default configuration
     *
     * @see JsonSchemaFactory#newBuilder()
     */
    JsonSchemaFactoryBuilder()
    {
        reportProvider = new ListReportProvider(LogLevel.INFO, LogLevel.FATAL);
        loadingCfg = LoadingConfiguration.byDefault();
        validationCfg = ValidationConfiguration.byDefault();
    }

    /**
     * A builder spawned from an existing {@link JsonSchemaFactory}
     *
     * @param factory the factory
     * @see JsonSchemaFactory#thaw()
     */
    JsonSchemaFactoryBuilder(final JsonSchemaFactory factory)
    {
        reportProvider = factory.reportProvider;
        loadingCfg = factory.loadingCfg;
        validationCfg = factory.validationCfg;
    }

    /**
     * Set a new report provider for this factory
     *
     * @param reportProvider the report provider
     * @return this
     * @throws FactoryConfigurationError provider is null
     */
    public JsonSchemaFactoryBuilder setReportProvider(
        final ReportProvider reportProvider)
    {
        if (reportProvider == null)
            throw new FactoryConfigurationError(new ProcessingMessage()
                .message(NULL_REPORT_PROVIDER));
        this.reportProvider = reportProvider;
        return this;
    }

    /**
     * Set a new loading configuration for this factory
     *
     * @param loadingCfg the loading configuration
     * @return this
     * @throws FactoryConfigurationError configuration is null
     */
    public JsonSchemaFactoryBuilder setLoadingConfiguration(
        final LoadingConfiguration loadingCfg)
    {
        if (loadingCfg == null)
            throw new FactoryConfigurationError(new ProcessingMessage()
                .message(NULL_LOADING_CFG));
        this.loadingCfg = loadingCfg;
        return this;
    }

    /**
     * Set a new validation configuration for this factory
     *
     * @param validationCfg the validation configuration
     * @return this
     * @throws FactoryConfigurationError configuration is null
     */
    public JsonSchemaFactoryBuilder setValidationConfiguration(
        final ValidationConfiguration validationCfg)
    {
        if (validationCfg == null)
            throw new FactoryConfigurationError(new ProcessingMessage()
                .message(NULL_VALIDATION_CFG));
        this.validationCfg = validationCfg;
        return this;
    }

    /**
     * Build a frozen instance of this factory configuration
     *
     * @return a new {@link JsonSchemaFactory}
     * @see JsonSchemaFactory#JsonSchemaFactory(JsonSchemaFactoryBuilder)
     */
    @Override
    public JsonSchemaFactory freeze()
    {
        return new JsonSchemaFactory(this);
    }
}
