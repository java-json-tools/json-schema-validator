package com.github.fge.jsonschema.testsuite;

import com.github.fge.jsonschema.SchemaVersion;

import java.io.IOException;

public final class DraftV3TestSuite
    extends TestSuite
{
    public DraftV3TestSuite()
        throws IOException
    {
        super(SchemaVersion.DRAFTV3, "draftv3");
    }
}
