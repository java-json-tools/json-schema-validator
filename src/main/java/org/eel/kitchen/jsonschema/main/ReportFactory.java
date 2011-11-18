package org.eel.kitchen.jsonschema.main;

public final class ReportFactory
{
    private final boolean failFast;

    public ReportFactory(final boolean failFast)
    {
        this.failFast = failFast;
    }

    public ValidationReport create(final String prefix)
    {
        return failFast
            ? new FailFastValidationReport()
            : new FullValidationReport(prefix);

    }
}
