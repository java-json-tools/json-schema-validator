package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.jsonschema.validators.combined.CombinedValidator;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

public class AbstractValidatorFactory
    implements ValidatorFactory
{
    protected final JsonNode schemaNode;
    protected final Class<? extends Validator> typeValidator;
    protected final LinkedList<Class<? extends Validator>> validatorList
        = new LinkedList<Class<? extends Validator>>();

    AbstractValidatorFactory(final JsonNode schemaNode,
        final Class<? extends Validator> typeValidator)
    {
        this.schemaNode = schemaNode;
        this.typeValidator = typeValidator;
        validatorList.add(typeValidator);
    }

    @Override
    public Validator getValidator()
    {
        final LinkedList<Validator> validators
            = new LinkedList<Validator>();

        Validator v;
        Constructor<? extends Validator> constructor;

        for (final Class<? extends Validator> c: validatorList) {
            try {
                constructor = c.getConstructor(JsonNode.class);
                v = constructor.newInstance(schemaNode);
                v.setup();
                validators.add(v);
            } catch (Exception e) {
                return new IllegalSchemaValidator(e);
            }
        }
        return new CombinedValidator(validators);
    }

}
