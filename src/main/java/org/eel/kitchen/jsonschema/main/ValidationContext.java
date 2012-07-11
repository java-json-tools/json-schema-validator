/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.main;

import org.eel.kitchen.jsonschema.schema.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.util.JsonPointer;

import java.util.List;

public final class ValidationContext
{
    private static final JsonPointer ROOT;

    static {
        try {
            ROOT = new JsonPointer("#");
        } catch (JsonSchemaException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private SchemaContainer container;
    private JsonSchemaFactory factory;
    private final ValidationReport report;

    public ValidationContext()
    {
        report = new ValidationReport(new JsonPointer(ROOT));
    }

    public ValidationContext(final JsonPointer path)
    {
        report = new ValidationReport(path);
    }

    public ValidationContext(final ValidationContext other)
    {
        container = other.container;
        factory = other.factory;
        report = new ValidationReport(new JsonPointer(ROOT));
        report.setPath(other.getPath());
    }

    public void addMessage(final String message)
    {
        report.addMessage(message);
    }

    public void addMessages(final List<String> messages)
    {
        report.addMessages(messages);
    }

    public void setPath(final JsonPointer path)
    {
        report.setPath(path);
    }

    public JsonPointer getPath()
    {
        return report.getPath();
    }

    public boolean isSuccess()
    {
        return report.isSuccess();
    }

    public void mergeWith(final ValidationContext other)
    {
        report.mergeWith(other.report);
    }

    public List<String> getMessages()
    {
        return report.getMessages();
    }

    public void setFactory(final JsonSchemaFactory factory)
    {
        this.factory = factory;
    }

    public JsonSchemaFactory getFactory()
    {
        return factory;
    }

    public SchemaContainer getContainer()
    {
        return container;
    }

    public void setContainer(final SchemaContainer container)
    {
        this.container = container;
    }
}
