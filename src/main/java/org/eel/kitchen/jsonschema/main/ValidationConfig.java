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

package org.eel.kitchen.jsonschema.main;

import org.eel.kitchen.jsonschema.bundle.CustomValidatorBundle;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;
import org.eel.kitchen.util.SchemaVersion;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public final class ValidationConfig
{
    private SchemaVersion defaultVersion = SchemaVersion.DRAFT_V3;

    private final Map<SchemaVersion, ValidatorBundle> bundles
        = new EnumMap<SchemaVersion, ValidatorBundle>(SchemaVersion.class);

    private final EnumSet<ValidationFeature> features
        = EnumSet.noneOf(ValidationFeature.class);

    public ValidationConfig()
    {
        ValidatorBundle bundle;

        for (final SchemaVersion version: SchemaVersion.values()) {
            bundle = new CustomValidatorBundle(version.getBundle());
            bundles.put(version, bundle);
        }
    }

    public SchemaVersion getDefaultVersion()
    {
        return defaultVersion;
    }

    public void setDefaultVersion(final SchemaVersion defaultVersion)
    {
        this.defaultVersion = defaultVersion;
    }

    public Map<SchemaVersion, ValidatorBundle> getBundles()
    {
        return Collections.unmodifiableMap(bundles);
    }

    public void registerValidator(final String keyword,
        final SyntaxValidator sv, final KeywordValidator kv,
        final NodeType... types)
    {
        registerValidator(defaultVersion, keyword, sv, kv, types);
    }

    private void registerValidator(final SchemaVersion version,
        final String keyword, final SyntaxValidator sv,
        final KeywordValidator kv, final NodeType... types)
    {
        bundles.get(version).registerValidator(keyword, sv, kv, types);
    }

    public void unregisterValidator(final String keyword)
    {
        unregisterValidator(defaultVersion, keyword);
    }

    private void unregisterValidator(final SchemaVersion version,
        final String keyword)
    {
        bundles.get(version).unregisterValidator(keyword);
    }

    public boolean enable(final ValidationFeature feature)
    {
        return features.add(feature);
    }

    public boolean disable(final ValidationFeature feature)
    {
        return features.remove(feature);
    }

    public EnumSet<ValidationFeature> getFeatures()
    {
        return EnumSet.copyOf(features);
    }
}
