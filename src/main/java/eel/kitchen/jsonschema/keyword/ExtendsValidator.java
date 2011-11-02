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

package eel.kitchen.jsonschema.keyword;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.base.CombinedValidator;
import eel.kitchen.jsonschema.context.ValidationContext;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public final class ExtendsValidator
    extends CombinedValidator
{
    public ExtendsValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);
        buildQueue();
    }

    private void buildQueue()
    {
        final KeywordValidatorFactory factory = context.getKeywordFactory();
        final ObjectNode baseNode = JsonNodeFactory.instance.objectNode();

        baseNode.putAll((ObjectNode) schema);

        final JsonNode extendsNode = baseNode.remove("extends");

        ValidationContext other = context.createContext(baseNode);

        queue.add(factory.getValidator(other, instance));

        if (extendsNode.isObject()) {
            other = context.createContext(extendsNode);
            queue.add(factory.getValidator(other, instance));
            return;
        }

        for (final JsonNode node: extendsNode) {
            other = context.createContext(node);
            queue.add(factory.getValidator(other, instance));
        }
    }

    @Override
    public ValidationReport validate()
    {
        while (hasMoreElements()) {
            report.mergeWith(nextElement().validate());
            if (!report.isSuccess())
                break;
        }

        queue.clear();
        return report;
    }
}
