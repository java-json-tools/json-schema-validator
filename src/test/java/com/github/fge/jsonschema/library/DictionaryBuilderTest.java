package com.github.fge.jsonschema.library;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class DictionaryBuilderTest
{
    private static final String KEY = "key";
    private static final Whatever MOCK1 = mock(Whatever.class);
    private static final Whatever MOCK2 = mock(Whatever.class);

    private DictionaryBuilder<Whatever> builder;

    @BeforeMethod
    public void createBuilder()
    {
        builder = Dictionary.newBuilder();
    }

    @Test
    public void cannotInsertNullKey()
    {
        try {
            builder.addEntry(null, null);
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "key must not be null");
        }
    }

    @Test
    public void cannotInsertNullValue()
    {
        try {
            builder.addEntry(KEY, null);
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "value must not be null");
        }
    }

    @Test
    public void insertedValueCanBeRetrieved()
    {
        builder.addEntry(KEY, MOCK1);
        assertSame(builder.freeze().get(KEY), MOCK1);
    }

    @Test
    public void removedValueCannotBeRetrieved()
    {
        builder.addEntry(KEY, MOCK1);
        builder.removeEntry(KEY);
        assertNull(builder.freeze().get(KEY));
    }

    @Test
    public void valuesCanBeOverwritten()
    {
        builder.addEntry(KEY, MOCK1);
        builder.addEntry(KEY, MOCK2);
        assertSame(builder.freeze().get(KEY), MOCK2);
    }

    private interface Whatever
    {
    }
}
