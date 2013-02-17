package com.github.fge.jsonschema.util;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.testng.Assert.assertEquals;

public final class RhinoHelperTest
{
    @DataProvider
    public Iterator<Object[]> ecma262regexes()
    {
        return ImmutableList.of(
            new Object[] { "[^]", true },
            new Object[] { "(?<=foo)bar", false },
            new Object[] { "", true },
            new Object[] { "[a-z]+(?!foo)(?=bar)", true }
        ).iterator();
    }

    @Test(
        dataProvider = "ecma262regexes",
        invocationCount = 10,
        threadPoolSize = 4
    )
    public void regexesAreCorrectlyAnalyzed(final String regex,
        final boolean valid)
    {
        assertEquals(RhinoHelper.regexIsValid(regex), valid);
    }

    @DataProvider
    public Iterator<Object[]> regexTestCases()
    {
        return ImmutableList.of(
            new Object[] { "[^a-z]", "9am", true },
            new Object[] { "bar\\d+", "foobar19ae", true },
            new Object[] { "^bar\\d+", "bar", false },
            new Object[] { "[a-z]+(?!foo)(?=bar)", "3aaaabar", true }
        ).iterator();
    }

    @Test(
        dataProvider = "regexTestCases",
        invocationCount = 10,
        threadPoolSize = 4
    )
    public void regexMatchingIsDoneCorrectly(final String regex,
        final String input, final boolean valid)
    {
        assertEquals(RhinoHelper.regMatch(regex, input), valid);
    }
}
