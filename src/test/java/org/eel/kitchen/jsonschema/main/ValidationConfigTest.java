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
