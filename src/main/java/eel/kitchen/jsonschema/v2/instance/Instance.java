package eel.kitchen.jsonschema.v2.instance;

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

public interface Instance
    extends Iterable<Instance>
{
    JsonNode getRawInstance();

    NodeType getType();

    String getPathElement();

    String getAbsolutePath();
}
