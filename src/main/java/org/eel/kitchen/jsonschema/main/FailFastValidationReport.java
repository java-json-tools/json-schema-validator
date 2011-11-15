package org.eel.kitchen.jsonschema.main;

import java.util.Collections;
import java.util.List;

public final class FailFastValidationReport
    implements ValidationReport
{
    @Override
    public ValidationStatus getStatus()
    {
        return ValidationStatus.SUCCESS;
    }

    @Override
    public boolean isSuccess()
    {
        return true;
    }

    @Override
    public boolean isError()
    {
        return false;
    }

    @Override
    public List<String> getMessages()
    {
        return Collections.emptyList();
    }

    @Override
    public void addMessage(final String message)
    {
        throw new FailFastValidationException(message);
    }

    @Override
    public void error(final String message)
    {
        throw new FailFastValidationException(message);
    }

    @Override
    public void mergeWith(final ValidationReport other)
    {
    }
}
