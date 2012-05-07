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

import org.eel.kitchen.jsonschema.factories.ValidatorFactory;
import org.eel.kitchen.util.SchemaVersion;
import org.testng.annotations.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public final class ValidationConfigTest
{
    @Test
    public void testFactoriesBuiltOnlyOnce()
    {
        final ValidationConfig cfg = new ValidationConfig();
        final Map<SchemaVersion, ValidatorFactory> m1, m2;

        cfg.buildFactories();
        m1 = new EnumMap<SchemaVersion, ValidatorFactory>(SchemaVersion.class);
        for (final SchemaVersion version: SchemaVersion.values())
            m1.put(version, cfg.getFactory(version));

        cfg.buildFactories();
        m2 = new EnumMap<SchemaVersion, ValidatorFactory>(SchemaVersion.class);
        for (final SchemaVersion version: SchemaVersion.values())
            m2.put(version, cfg.getFactory(version));

        assertEquals(m1, m2);
    }
}
