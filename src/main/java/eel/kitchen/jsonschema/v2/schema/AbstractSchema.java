package eel.kitchen.jsonschema.v2.schema;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

abstract class AbstractSchema implements Schema
{
    protected final List<String> messages = new LinkedList<String>();

    @Override
    public final List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
}
