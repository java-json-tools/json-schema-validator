package eel.kitchen.jsonschema;

import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.jsonschema.validators.type.ArrayValidator;
import eel.kitchen.jsonschema.validators.type.BooleanValidator;
import eel.kitchen.jsonschema.validators.type.IntegerValidator;
import eel.kitchen.jsonschema.validators.type.NullValidator;
import eel.kitchen.jsonschema.validators.type.NumberValidator;
import eel.kitchen.jsonschema.validators.type.ObjectValidator;
import eel.kitchen.jsonschema.validators.type.StringValidator;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static eel.kitchen.util.NodeType.ARRAY;
import static eel.kitchen.util.NodeType.BOOLEAN;
import static eel.kitchen.util.NodeType.INTEGER;
import static eel.kitchen.util.NodeType.NULL;
import static eel.kitchen.util.NodeType.NUMBER;
import static eel.kitchen.util.NodeType.OBJECT;
import static eel.kitchen.util.NodeType.STRING;

public final class SchemaNodeFactory
{
    private static final Logger logger
        = LoggerFactory.getLogger(SchemaNodeFactory.class);

    private final Map<NodeType, Set<Class<? extends Validator>>> registeredValidators
        = new EnumMap<NodeType, Set<Class<? extends Validator>>>(NodeType.class);
    private final Map<String, EnumSet<NodeType>> registeredTypes
        = new HashMap<String, EnumSet<NodeType>>();

    public SchemaNodeFactory()
    {
        registerValidator("array", ArrayValidator.class, ARRAY);
        registerValidator("boolean", BooleanValidator.class, BOOLEAN);
        registerValidator("integer", IntegerValidator.class, INTEGER);
        registerValidator("number", NumberValidator.class, NUMBER);
        registerValidator("null", NullValidator.class, NULL);
        registerValidator("object", ObjectValidator.class, OBJECT);
        registerValidator("string", StringValidator.class, STRING);
    }


    private void registerValidator(final String type,
        final Class<? extends Validator> validator, final NodeType... nodeTypes)
    {
        if (registeredTypes.containsKey(type)) {
            logger.error("type {} already registered");
            return;
        }

        final EnumSet<NodeType> types = EnumSet.copyOf(Arrays.asList(nodeTypes));

        registeredTypes.put(type, types);
        final Set<Class<? extends Validator>> set
            = new LinkedHashSet<Class<? extends Validator>>();
        set.add(validator);

        for (final NodeType nodeType: types)
            if (registeredValidators.containsKey(nodeType))
                registeredValidators.get(nodeType).addAll(set);
            else
                registeredValidators.put(nodeType, set);
    }

    public SchemaNode getSchemaNode(final JsonNode schema)
    {
        return new SchemaNode(schema, registeredValidators, registeredTypes);
    }
}
