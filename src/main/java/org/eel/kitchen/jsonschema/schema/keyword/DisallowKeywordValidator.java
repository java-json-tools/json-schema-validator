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

package org.eel.kitchen.jsonschema.schema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.jsonschema.schema.ValidationReport;
import org.eel.kitchen.util.NodeType;

public final class DisallowKeywordValidator
    extends AbstractTypeKeywordValidator
{
    public DisallowKeywordValidator(final JsonNode schema)
    {
        super("disallow", schema);
    }

    @Override
    public void validate(final ValidationReport report,
        final JsonNode instance)
    {
        if (typeSet.contains(NodeType.getNodeType(instance))) {
            report.addMessage("instance is of a disallowed primitive type");
            return;
        }

        ValidationReport tmp;

        for (final JsonNode schema: schemas) {
            tmp = new ValidationReport();
            JsonSchema.fromNode(report.getSchema(), schema)
                .validate(tmp, instance);
            if (tmp.isSuccess()) {
                report.addMessage("instance matches a disallowed schema");
                return;
            }
        }
    }

}
