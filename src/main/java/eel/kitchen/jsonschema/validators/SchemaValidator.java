package eel.kitchen.jsonschema.validators;

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SchemaValidator
    implements Validator
{
    private static final String ANY_TYPE = "any";
    private JsonNode schema;
    private final List<String> messages = new ArrayList<String>();
    private final Set<String> registeredTypes = new HashSet<String>();
    private boolean setupDone, validSchema;

    public SchemaValidator(final Set<String> extraTypes)
    {
        registeredTypes.addAll(extraTypes);
    }

    @Override
    public Validator setSchema(final JsonNode schema)
    {
        messages.clear();
        setupDone = false;
        validSchema = false;
        this.schema = schema;
        return this;
    }

    @Override
    public boolean setup()
    {
        if (!setupDone)
            validSchema = doSetup();
        return validSchema;
    }

    private boolean doSetup()
    {
        messages.clear();
        if (schema == null) {
            messages.add("schema is null");
            return false;
        }

        if (!schema.isObject()) {
            messages.add("schema is not an object");
            return false;
        }

        return validateTypeElement("type") && validateTypeElement("disallow");
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        return setup();
    }

    private boolean validateTypeElement(final String field)
    {
        final JsonNode node = schema.get(field);

        if (node == null)
            return true;

        if (node.isTextual())
            return validateTypeName(node.getTextValue());

        if (!node.isArray()) {
            messages.add("type property is neither a string nor an array");
            return false;
        }

        for (final JsonNode element: node) {
            if (!element.isTextual()) {
                messages.add(String.format("non string element in %s property "
                    + "array", field));
                return false;
            }
            if (!validateTypeName(element.getTextValue()))
                return false;
        }

        return true;
    }

    private boolean validateTypeName(final String textValue)
    {
        try {
            if (ANY_TYPE.equals(textValue))
                return true;
            if (registeredTypes.contains(textValue))
                return true;
            NodeType.valueOf(textValue.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            messages.add(String.format("unknown type %s", textValue));
            return false;
        }
    }

    @Override
    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public SchemaProvider getSchemaProvider()
    {
        return new EmptySchemaProvider();
    }
}
