/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.v2.schema;

import eel.kitchen.jsonschema.v2.instance.Instance;
import eel.kitchen.jsonschema.v2.keyword.KeywordValidator;
import eel.kitchen.jsonschema.v2.keyword.ValidationStatus;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static eel.kitchen.jsonschema.v2.keyword.ValidationStatus.*;

public final class ValidationState
{
    private final SchemaFactory factory;
    private final List<String> messages = new LinkedList<String>();
    private ValidationStatus status = DUNNO;
    private Schema nextSchema;

    public ValidationState(final SchemaFactory factory)
    {
        this.factory = factory;
    }

    public ValidationState(final ValidationState other)
    {
        factory = other.factory;
    }

    public void mergeWith(final ValidationState other)
    {
        if (!other.isFailure())
            return;

        messages.addAll(other.messages);
        status = FAILURE;
    }

    public void compatMergeWith(final Schema schema, final Instance instance)
    {
        final boolean ret = schema.validate(instance);

        if (ret)
            return;

        messages.addAll(schema.getMessages());
        status = FAILURE;
    }

    public SchemaFactory getFactory()
    {
        return factory;
    }

    public void addMessage(final String message)
    {
        messages.add(message);
    }

    public void addMessages(final List<String> messages)
    {
        this.messages.addAll(messages);
    }

    public void clearMessages()
    {
        messages.clear();
    }

    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    public ValidationStatus getStatus()
    {
        return status;
    }

    public void setStatus(final ValidationStatus status)
    {
        this.status = status;

        if (status != DUNNO)
            nextSchema = null;

        if (status == SUCCESS)
            messages.clear();
    }

    public boolean isResolved()
    {
        return status != DUNNO;
    }

    public boolean isFailure()
    {
        return status == FAILURE || status == ERROR;
    }

    public Schema getNextSchema()
    {
        if (nextSchema == null)
            throw new RuntimeException(".getNextSchema() called without a "
                + "schema to give back");
        return nextSchema;
    }

    public void setNextSchema(final Schema nextSchema)
    {
        this.nextSchema = nextSchema;
    }
}
