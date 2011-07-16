package eel.kitchen.jsonschema;

import eel.kitchen.jsonschema.validators.SchemaProvider;
import eel.kitchen.jsonschema.validators.SchemaValidator;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SchemaNode
{
    private static final String ANY_TYPE = "any";

    private final Map<NodeType, Set<Class<? extends Validator>>> ctors
        = new EnumMap<NodeType, Set<Class<? extends Validator>>>(NodeType.class);
    private final Map<String, EnumSet<NodeType>> types
        = new HashMap<String, EnumSet<NodeType>>();

    private final JsonNode schema;
    private final Map<NodeType, Set<Validator>> validators
        = new EnumMap<NodeType, Set<Validator>>(NodeType.class);

    private final List<String> messages = new LinkedList<String>();

    private boolean brokenSchema = false;
    private Validator successful;

    public SchemaNode(final JsonNode schema,
        final Map<NodeType, Set<Class<? extends Validator>>> allValidators,
        final Map<String, EnumSet<NodeType>> allTypes)
    {
        this.schema = schema;
        final Validator schemaValidator = new SchemaValidator(allTypes.keySet());
        if (!schemaValidator.setSchema(schema).setup()) {
            messages.addAll(schemaValidator.getMessages());
            brokenSchema = true;
            return;
        }

        ctors.putAll(allValidators);
        types.putAll(allTypes);

        setup();
    }

    private void setup()
    {
        final Set<String>
            typeNames = getValidatingTypes(),
            allTypes = new HashSet<String>(types.keySet());

        allTypes.removeAll(typeNames);
        EnumSet<NodeType> goodbye;

        for (final String unsupported: allTypes) {
            goodbye = types.remove(unsupported);
            for (final NodeType nodeType: goodbye)
                ctors.remove(nodeType);
        }

        if (ctors.isEmpty()) {
            messages.add("schema does not allow any type??");
            brokenSchema = true;
            return;
        }

        buildValidators();
    }

    private void buildValidators()
    {
        boolean oops;

        for (final NodeType type: ctors.keySet()) {
            oops = false;
            try {
                validators.put(type, validatorSet(ctors.get(type)));
            } catch (NoSuchMethodException e) {
                oops = true;
            } catch (InvocationTargetException e) {
                oops = true;
            } catch (IllegalAccessException e) {
                oops = true;
            } catch (InstantiationException e) {
                oops = true;
            }
            if (oops) {
                messages.add("cannot instantiate validators");
                brokenSchema = true;
                return;
            }
        }
    }

    private Set<Validator> validatorSet(final Set<Class<? extends Validator>> set)
        throws NoSuchMethodException, InvocationTargetException,
        IllegalAccessException, InstantiationException
    {
        final Set<Validator> result = new LinkedHashSet<Validator>();
        Validator v;

        for (final Class<? extends Validator> c: set) {
            v = c.getConstructor().newInstance();
            v.setSchema(schema);
            result.add(v);
        }

        return result;
    }

    private Set<String> getValidatingTypes()
    {
        final Set<String> ret = new HashSet<String>();

        final JsonNode
            typeNode = schema.get("type"),
            disallowNode = schema.get("disallow");

        if (typeNode == null)
            ret.addAll(types.keySet());
        else if (typeNode.isTextual())
            ret.add(typeNode.getTextValue());
        else
            for (final JsonNode element: typeNode)
                ret.add(element.getTextValue());

        if (ret.remove(ANY_TYPE))
            ret.addAll(types.keySet());

        if (disallowNode == null)
            return ret;

        final Set<String> disallow = new HashSet<String>();

        if (disallowNode.isTextual())
            disallow.add(disallowNode.getTextValue());
        else
            for (final JsonNode element: disallowNode)
                disallow.add(element.getTextValue());

        if (disallow.remove(ANY_TYPE))
            return Collections.emptySet();

        ret.removeAll(disallow);
        if (ret.contains("integer") && disallow.contains("number"))
            ret.remove("integer");

        return ret;
    }

    public boolean isValid()
    {
        if (brokenSchema)
            return false;

        boolean ret = true;

        for (final NodeType nodeType: validators.keySet()) {
            for (final Validator v: validators.get(nodeType)) {
                if (!v.setup()) {
                    messages.addAll(v.getMessages());
                    ret = false;
                }
            }
        }

        return ret;
    }

    public boolean validate(final JsonNode node)
    {
        if (!isValid())
            return false;

        if (node == null) {
            messages.add("JSON to validate is null");
            return false;
        }

        final NodeType nodeType = NodeType.getNodeType(node);

        if (!validators.containsKey(nodeType)) {
            messages.add(String.format("node is of type %s, expected %s",
                nodeType, validators.keySet()));
            return false;
        }

        final Set<Validator> set = validators.get(nodeType);

        for (final Validator v: set) {
            if (v.validate(node)) {
                successful = v;
                messages.clear();
                return true;
            }
            messages.addAll(v.getMessages());
        }
        return false;
    }

    public SchemaProvider getSchemaProvider()
    {
        return successful.getSchemaProvider();
    }

    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
}
