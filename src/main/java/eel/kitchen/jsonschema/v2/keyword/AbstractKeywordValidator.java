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

package eel.kitchen.jsonschema.v2.keyword;

import eel.kitchen.jsonschema.v2.schema.Schema;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

abstract class AbstractKeywordValidator
    implements KeywordValidator
{
    protected final JsonNode schema;

    protected final List<String> messages = new LinkedList<String>();

    protected AbstractKeywordValidator(final JsonNode schema)
    {
        this.schema = schema;
    }

    protected abstract void setup();

    protected abstract ValidationStatus doValidate(final JsonNode instance);

    @Override
    public final ValidationStatus validate(final JsonNode instance)
    {
        setup();

        return doValidate(instance);
    }

    @Override
    public final List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public Schema getNextSchema()
    {
        throw new IllegalArgumentException("I should have been implemented");
    }
}
