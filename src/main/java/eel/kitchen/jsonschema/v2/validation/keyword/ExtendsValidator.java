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

package eel.kitchen.jsonschema.v2.validation.keyword;

import eel.kitchen.jsonschema.v2.validation.ValidationReport;
import eel.kitchen.jsonschema.v2.validation.ValidatorFactory;
import eel.kitchen.jsonschema.v2.validation.base.CombinedValidator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public final class ExtendsValidator
    extends CombinedValidator
{
    public ExtendsValidator(final ValidatorFactory factory,
        final JsonNode schema, final JsonNode instance)
    {
        super(factory, schema, instance);
        buildQueue();
    }

    private void buildQueue()
    {
        final ObjectNode baseNode = (ObjectNode) schema;
        final JsonNode extendsNode = baseNode.remove("extends");

        queue.add(factory.getValidator(baseNode, instance));

        if (extendsNode.isObject()) {
            queue.add(factory.getValidator(extendsNode, instance));
            return;
        }

        for (final JsonNode node: extendsNode)
            queue.add(factory.getValidator(node, instance));
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
