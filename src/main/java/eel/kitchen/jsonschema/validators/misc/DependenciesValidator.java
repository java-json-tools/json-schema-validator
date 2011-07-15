package eel.kitchen.jsonschema.validators.misc;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.IterableJsonNode;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DependenciesValidator
    extends AbstractValidator
{
    private final Map<String, Set<String>> dependencies
        = new HashMap<String, Set<String>>();

    public DependenciesValidator()
    {
        registerField("dependencies", NodeType.OBJECT);
    }

    @Override
    protected boolean doSetup()
    {
        if (!super.doSetup())
            return false;

        dependencies.clear();

        final JsonNode depsNode = schema.get("dependencies");
        if (depsNode == null)
            return true;

        final IterableJsonNode inode = new IterableJsonNode(depsNode);

        String fieldName;
        Set<String> set;

        for (final Map.Entry<String, JsonNode> entry: inode) {
            fieldName = entry.getKey();
            try {
                set = computeOneDependency(entry.getValue());
            } catch (MalformedJasonSchemaException e) {
                messages.add(e.getMessage());
                return false;
            }
            set.remove(fieldName);
            dependencies.put(fieldName, set);
        }

        return true;
    }

    private static Set<String> computeOneDependency(final JsonNode node)
        throws MalformedJasonSchemaException
    {
        final Set<String> ret = new HashSet<String>();

        if (node.isTextual()) {
            ret.add(node.getTextValue());
            return ret;
        }

        if (!node.isArray())
            throw new MalformedJasonSchemaException("dependency value should "
                + "be a string or an array");

        for (final JsonNode element: node) {
            if (!element.isTextual())
                throw new MalformedJasonSchemaException("dependency "
                    + "array elements should be strings");
            ret.add(element.getTextValue());
        }

        return ret;
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        boolean ret = true;

        final Set<String>
            checks = new HashSet<String>(dependencies.keySet()),
            existing = CollectionUtils.toSet(node.getFieldNames());

        checks.retainAll(existing);

        final Set<String> deps = new HashSet<String>();

        for (final String field: checks) {
            deps.clear();
            deps.addAll(dependencies.get(field));
            deps.removeAll(existing);
            if (deps.isEmpty())
                continue;
            ret = false;
            for (final String missing: deps)
                messages.add(String.format("property %s depends on %s, "
                    + "but the latter was not found", field, missing));
        }

        return ret;
    }
}
