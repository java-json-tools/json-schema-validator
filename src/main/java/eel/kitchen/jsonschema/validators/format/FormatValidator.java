package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FormatValidator
    extends AbstractValidator
{
    private static final Logger logger
        = LoggerFactory.getLogger(FormatValidator.class);

    private static final Map<NodeType, List<Class<? extends Validator>>> validators
        = new HashMap<NodeType, List<Class<? extends Validator>>>();

    private String format;

    static {
        registerFormat(NodeType.STRING, CSSColorValidator.class);
        registerFormat(NodeType.STRING, CSSStyleValidator.class);
        registerFormat(NodeType.STRING, DateFormatValidator.class);
        registerFormat(NodeType.STRING, EmailFormatValidator.class);
        registerFormat(NodeType.STRING, HostnameFormatValidator.class);
        registerFormat(NodeType.STRING, IPv4FormatValidator.class);
        registerFormat(NodeType.STRING, IPv6FormatValidator.class);
        registerFormat(NodeType.STRING, ISO8601DateFormatValidator.class);
        registerFormat(NodeType.STRING, PhoneNumberFormatValidator.class);
        registerFormat(NodeType.STRING, RegexFormatValidator.class);
        registerFormat(NodeType.STRING, TimeFormatValidator.class);
        registerFormat(NodeType.INTEGER, UnixEpochFormatValidator.class);
        registerFormat(NodeType.NUMBER, UnixEpochFormatValidator.class);
        registerFormat(NodeType.STRING, URIFormatValidator.class);
    }

    public FormatValidator()
    {
        registerField("format", NodeType.STRING);
    }

    private static void registerFormat(final NodeType type,
        final Class<? extends Validator> validator)
    {
        if (!validators.containsKey(type))
            validators.put(type, new ArrayList<Class<? extends Validator>>());

        validators.get(type).add(validator);
    }

    @Override
    protected boolean doSetup()
    {
        if (!super.doSetup())
            return false;

        format = schema.get("format").getTextValue();
        return true;
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        if (!setup())
            return false;

        messages.clear();

        final NodeType nodeType = NodeType.getNodeType(node);

        if (!validators.containsKey(nodeType)) {
            logger.warn("no format validators for node of type {}, "
                + "format validation ignored", nodeType);
            return true;
        }

        final String enumKey = format.replaceAll("-", "_").toUpperCase();
        final Class<? extends Validator> validatorClass;

        try {
            validatorClass = FormatPicker.valueOf(enumKey).getValidator();
        } catch (IllegalArgumentException e) {
            logger.error("No such format \"%s\", format validation ignored",
                format);
            return true;
        }

        if (!validators.get(nodeType).contains(validatorClass)) {
            logger.warn("format \"{}\" cannot validate nodes of type \"{}\", "
                + "format validation ignored", format, nodeType);
            return true;
        }

        final Validator v;
        final boolean ret;

        try {
            v = validatorClass.getConstructor().newInstance();
        } catch (Exception e) {
            messages.add(String.format("cannot instantiate validator: "
                + "%s: %s", e.getClass().getCanonicalName(), e.getMessage()));
            return false;
        }

        ret = v.validate(node);
        messages.addAll(v.getMessages());
        return ret;
    }
}
