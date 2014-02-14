package com.github.fge.jsonschema.format.extra;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.uritemplate.URITemplate;
import com.github.fge.uritemplate.URITemplateParseException;

public final class URITemplateFormatAttribute
    extends AbstractFormatAttribute
{
    private static final FormatAttribute INSTANCE
        = new URITemplateFormatAttribute();

    private URITemplateFormatAttribute()
    {
        super("uri-template", NodeType.STRING);
    }

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void validate(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final String value = data.getInstance().getNode().textValue();
        try {
            new URITemplate(value);
        } catch (URITemplateParseException ignored) {
            report.error(newMsg(data, bundle, "err.format.uriTemplate.invalid")
                .putArgument("value", value));
        }
    }
}
