package eel.kitchen.jsonschema.v2.schema;

import eel.kitchen.jsonschema.v2.instance.Instance;
import eel.kitchen.jsonschema.v2.keyword.KeywordValidator;
import eel.kitchen.jsonschema.v2.keyword.KeywordValidatorProvider;
import eel.kitchen.jsonschema.v2.keyword.ValidationStatus;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.EnumSet;
import java.util.Set;

public final class SingleSchema
    extends AbstractSchema
{
    private static final KeywordValidatorProvider validatorProvider
        = KeywordValidatorProvider.getInstance();

    private final SchemaFactory factory;
    private final JsonNode schemaNode;
    private final EnumSet<NodeType> typeSet = EnumSet.noneOf(NodeType.class);

    private PathProvider pathProvider = AtomicNodePathProvider.getInstance();

    public SingleSchema(final SchemaFactory factory, final JsonNode schemaNode,
        final EnumSet<NodeType> typeSet)
    {
        this.factory = factory;
        this.schemaNode = schemaNode;
        this.typeSet.addAll(typeSet);
    }

    @Override
    public JsonNode getRawSchema()
    {
        return schemaNode;
    }

    @Override
    public boolean canExpand()
    {
        return false;
    }

    @Override
    public Schema getSchema(final String path)
    {
        return factory.getSchema(pathProvider.getSchema(path));
    }

    @Override
    public boolean validate(final Instance instance)
    {
        final NodeType instanceType = instance.getType();

        if (!typeSet.contains(instanceType)) {
            messages.add("instance is of type " + instanceType
                + ", expected one of " + typeSet);
            return false;
        }

        final JsonNode node = instance.getRawInstance();

        final Set<KeywordValidator> validators
            = validatorProvider.getValidators(node, instanceType);

        boolean ret = true;

        for (final KeywordValidator validator: validators)
            if (validator.validate(node) != ValidationStatus.SUCCESS) {
                messages.addAll(validator.getMessages());
                ret = false;
            }

        if (!ret)
            return false;

        pathProvider = instanceType == NodeType.ARRAY
            ? new ArrayPathProvider(node)
            : new ObjectPathProvider(node);

        Schema schema;

        for (final Instance child: instance) {
            schema = getSchema(child.getPathElement());
            if (schema.validate(child))
                continue;
            messages.addAll(schema.getMessages());
            ret = false;
        }

        return ret;
    }
}
