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

package eel.kitchen.jsonschema.v2.validator;

import eel.kitchen.jsonschema.v2.instance.JsonInstance;
import eel.kitchen.jsonschema.v2.keyword.KeywordValidator;
import eel.kitchen.jsonschema.v2.keyword.KeywordValidatorProvider;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

final class JsonLeafValidator
    implements JsonValidator
{
    private static final KeywordValidatorProvider provider
        = KeywordValidatorProvider.getInstance();

    private final NodeType type;

    private final JsonNode schema;

    private final List<String> messages = new LinkedList<String>();

    public JsonLeafValidator(final NodeType type, final JsonNode schema)
    {
        this.type = type;
        this.schema = schema;
    }

    @Override
    public boolean validate(final JsonInstance instance)
    {
        return instance.accept(this);
    }

    @Override
    public boolean visit(final JsonInstance instance)
    {
        final NodeType nodeType = instance.getNodeType();

        if (type != nodeType) {
            messages.add("instance is of type " + nodeType + ", expected "
                + type);
            return false;
        }

        final JsonNode node = instance.getInstance();

        final Set<KeywordValidator> validators
            = provider.getValidators(schema, type);

        boolean ret = true;

        for (final KeywordValidator validator: validators)
            if (!validator.validate(node)) {
                messages.addAll(validator.getMessages());
                ret = false;
            }

        return ret;
    }

    @Override
    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
}
