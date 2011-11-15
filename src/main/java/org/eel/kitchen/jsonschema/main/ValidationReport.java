package org.eel.kitchen.jsonschema.main;

import java.util.List;

public interface ValidationReport
{
    ValidationStatus getStatus();

    boolean isSuccess();

    boolean isError();

    List<String> getMessages();

    void addMessage(String message);

    void error(String message);

    void mergeWith(ValidationReport other);
}
