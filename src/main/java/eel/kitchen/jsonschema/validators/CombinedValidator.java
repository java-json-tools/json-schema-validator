package eel.kitchen.jsonschema.validators;


import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import org.codehaus.jackson.JsonNode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class CombinedValidator
    implements Validator
{
    private final Validator typeValidator;
    private final LinkedList<Validator> validators
        = new LinkedList<Validator>();
    private final List<String> messages
        = new LinkedList<String>();

    public CombinedValidator(final LinkedList<Validator> validators)
    {
        this.validators.addAll(validators);
        typeValidator = validators.get(0);
    }

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
        for (final Validator v: validators)
            v.setup();
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        messages.clear();

        for (final Validator v: validators)
            if (!v.validate(node)) {
                messages.addAll(v.getValidationErrors());
                return false;
            }

        return true;
    }

    @Override
    public List<String> getValidationErrors()
    {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public SchemaProvider getSchemaProvider()
    {
        return typeValidator.getSchemaProvider();
    }
}
