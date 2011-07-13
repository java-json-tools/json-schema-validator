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

package eel.kitchen.jsonschema.validators;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractValidator
    implements Validator
{
    protected static final JsonNode EMPTY_SCHEMA;

    protected JsonNode schema;
    protected final List<String> messages = new LinkedList<String>();

    protected AbstractValidator()
    {
    }

    protected AbstractValidator(final JsonNode schema)
    {
        this.schema = schema;
    }

    static {
        try {
            EMPTY_SCHEMA = new ObjectMapper().readTree("{}");
        } catch (IOException e) {
            throw  new ExceptionInInitializerError();
        }
    }

    @Override
    public void setSchema(final JsonNode schema)
    {
        this.schema = schema;
    }

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        return false;
    }

    @Override
    public SchemaProvider getSchemaProvider()
    {
        return new EmptySchemaProvider();
    }

    @Override
    public final List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
}
