package eel.kitchen.jsonschema.validators.misc;


import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.jsonschema.validators.SchemaProvider;
import eel.kitchen.jsonschema.validators.Validator;
import org.codehaus.jackson.JsonNode;

import java.util.LinkedList;
import java.util.List;

public final class CombinedValidator
    extends AbstractValidator
{
    private final Validator typeValidator;
    private final List<Validator> validators = new LinkedList<Validator>();

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
    public boolean isWellFormed()
    {
        messages.clear();

        for (final Validator v: validators)
            if (!v.isWellFormed()) {
                messages.addAll(v.getMessages());
                return false;
            }

        return true;
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        messages.clear();

        for (final Validator v: validators) {
            if (!v.isWellFormed()) {
                messages.addAll(v.getMessages());
                return false;
            }
            if (!v.validate(node)) {
                messages.addAll(v.getMessages());
                return false;
            }
        }

        return true;
    }

    @Override
    public SchemaProvider getSchemaProvider()
    {
        return typeValidator.getSchemaProvider();
    }
}
