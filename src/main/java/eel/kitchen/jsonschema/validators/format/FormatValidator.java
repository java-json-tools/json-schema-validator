package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.jsonschema.validators.type.AbstractTypeValidator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FormatValidator
    extends AbstractTypeValidator
{
    private static final Logger logger
        = LoggerFactory.getLogger(FormatValidator.class);

    private static final Map<String, List<Class<? extends Validator>>> validators
        = new HashMap<String, List<Class<? extends Validator>>>();

    private Class<? extends Validator> validatorClass;
    private String format;

    static {
        registerFormat("string", CSSColorValidator.class);
        registerFormat("string", CSSStyleValidator.class);
        registerFormat("string", DateFormatValidator.class);
        registerFormat("string", EmailFormatValidator.class);
        registerFormat("string", HostnameFormatValidator.class);
        registerFormat("string", IPv4FormatValidator.class);
        registerFormat("string", IPv6FormatValidator.class);
        registerFormat("string", ISO8601DateFormatValidator.class);
        registerFormat("string", PhoneNumberFormatValidator.class);
        registerFormat("string", RegexFormatValidator.class);
        registerFormat("string", TimeFormatValidator.class);
        registerFormat("integer", UnixEpochFormatValidator.class);
        registerFormat("number", UnixEpochFormatValidator.class);
        registerFormat("string", URIFormatValidator.class);
    }

    public FormatValidator(final JsonNode schemaNode)
    {
        super(schemaNode);
    }

    private static void registerFormat(final String type,
        final Class<? extends Validator> validator)
    {
        if (!validators.containsKey(type))
            validators.put(type, new ArrayList<Class<? extends Validator>>());

        validators.get(type).add(validator);
    }

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
        final JsonNode value = schemaNode.get("format");

        if (!value.isTextual())
            throw new MalformedJasonSchemaException("format node is not a "
                + "string");

        format = value.getTextValue();
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        validationErrors.clear();

        final String nodeType = JasonHelper.getNodeType(node);

        if (!validators.containsKey(nodeType)) {
            logger.warn("no format validators for node of type {}, "
                + "format validation ignored");
            return true;
        }

        final String enumKey = format.replaceAll("-", "_").toUpperCase();

        try {
            validatorClass = FormatPicker.valueOf(enumKey).getValidator();
        } catch (IllegalArgumentException e) {
            logger.error("No such format \"%s\", format validation ignored",
                format);
        }

        if (!validators.get(nodeType).contains(validatorClass)) {
            logger.warn("format \"{}\" cannot validate nodes of type \"{}\", "
                + "format validation ignored", format, nodeType);
            return true;
        }

        final Constructor<? extends Validator> constructor;
        final Validator v;
        final boolean ret;

        try {
            constructor = validatorClass.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException e) {
            validationErrors.add("cannot find constructor: " + e.getMessage());
            return false;
        }

        try {
            v = constructor.newInstance(schemaNode);
        } catch (Exception e) {
            validationErrors.add(String.format("cannot instantiate validator: "
                + "%s: %s", e.getClass().getCanonicalName(), e.getMessage()));
            return false;
        }

        ret = v.validate(node);
        validationErrors.addAll(v.getValidationErrors());
        return ret;
    }
}
