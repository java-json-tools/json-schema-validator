package org.eel.kitchen.jsonschema.main;

import java.util.Collections;
import java.util.List;

public final class FailFastValidationReport
    extends ValidationReport
{
    @Override
    public List<String> getMessages()
    {
        return Collections.emptyList();
    }

    @Override
    public void message(final String message)
    {
    }

    @Override
    public void fail()
        throws JsonValidationFailureException
    {
        throw new JsonValidationFailureException();
    }

    @Override
    public void fail(final String message)
        throws JsonValidationFailureException
    {
        throw new JsonValidationFailureException(message);
    }

    @Override
    public void error(final String message)
        throws JsonValidationFailureException
    {
        throw new JsonValidationFailureException(message);
    }

    @Override
    public void mergeWith(final ValidationReport other)
    {
    }
}
