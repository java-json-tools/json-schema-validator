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

package org.eel.kitchen.jsonschema.syntax.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.keyword.common.EnumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.UniqueItemsKeywordValidator;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.AbstractSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.util.jackson.JsonNodeEquivalence;

import java.util.List;
import java.util.Set;

/**
 * Syntax validator for the {@code enum} keyword
 *
 * <p>At the moment, this class has the same bug as {@link EnumKeywordValidator}
 * and {@link UniqueItemsKeywordValidator} with regards to numeric instances.
 * </p>
 */
public final class EnumSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonNodeEquivalence.getInstance();

    private static final SyntaxChecker INSTANCE = new EnumSyntaxChecker();

    private EnumSyntaxChecker()
    {
        super("enum", NodeType.ARRAY);
    }

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void checkValue(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema)
    {
        final JsonNode enumNode = schema.get(keyword);
        final Message.Builder msg = newMsg();

        if (enumNode.size() == 0) {
            msg.setMessage("an enum array must have at least one element");
            messages.add(msg.build());
            return;
        }

        /*
         * NOTE: we choose NOT to display the culprit element. The (admittedly
         * convoluted) reason is that said element, as per enum rules,
         * may be an arbitrary JSON document -- ie, as large as you can fathom.
         *
         * TODO: we may do with displaying the index in the array, that's better
         * than nothing...
         */
        final Set<Equivalence.Wrapper<JsonNode>> values = Sets.newHashSet();

        for (final JsonNode value: enumNode) {
            if (values.add(EQUIVALENCE.wrap(value)))
                continue;
            msg.setMessage("elements in the array are not unique");
            messages.add(msg.build());
            return;
        }

    }
}
