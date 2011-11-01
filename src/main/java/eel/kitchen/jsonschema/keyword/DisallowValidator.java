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
import eel.kitchen.jsonschema.ValidatorFactory;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

public final class DisallowValidator
    extends TypeKeywordValidator
{
    public DisallowValidator(final ValidatorFactory factory,
        final JsonNode schema, final JsonNode instance)
    {
        super(factory, schema, instance, "disallow");
    }

    @Override
    public ValidationReport validate()
    {
        final NodeType type = NodeType.getNodeType(instance);

        if (typeSet.contains(type)) {
            schemas.clear();
            report.addMessage("instance is of type " + type + " which is "
                + "explicitly disallowed");
            return report;
        }

        buildQueue();

        boolean matchFound = false;

        while (!matchFound && hasMoreElements())
            matchFound = nextElement().validate().isSuccess();

        queue.clear();

        if (matchFound)
            report.addMessage("instance is valid against a disallowed schema");

        return report;
    }
}
