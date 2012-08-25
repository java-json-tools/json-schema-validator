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

package org.eel.kitchen.jsonschema.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

/**
 * {@code id} fragment resolution class
 *
 * <p>JSON Schema documents can have {@code id} members in any subschema
 * (there can also be one at the root of the schema,
 * but this one plays a particular role). This class helps find a subschema
 * with a given id.</p>
 *
 * <p>Note that the draft does not specify anywhere that {@code id} members
 * should have unique values in a same schema! This implementation therefore
 * returns the first schema found with the given id, and you should <b>not</b>
 * rely on the order in which keys are looked up etc (there is no order
 * defined in the keys of a JSON Object!).
 * </p>
 */
final class IdFragment
    extends JsonFragment
{
    private final String id;

    IdFragment(final String id)
    {
        this.id = id;
    }

    @Override
    public JsonNode resolve(final JsonNode node)
    {
        /*
         * Non object instances are not worth being considered
         */
        if (!node.isObject())
            return MissingNode.getInstance();

        /*
         * If an id node exists and is a text node, see if that node's value
         * (minus the initial #) matches our id, if yes we have a match
         */
        final JsonNode idNode = node.path("id");

        if (idNode.isTextual()) {
            final String s = idNode.textValue().replaceFirst("^#", "");
            if (id.equals(s))
                return node;
        }

        /*
         * Otherwise, go on with children. As this is an object,
         * JsonNode will cycle through the values, which is what we want.
         */

        JsonNode ret;
        for (final JsonNode subNode: node) {
            ret = resolve(subNode);
            if (!ret.isMissingNode())
                return ret;
        }

        return MissingNode.getInstance();
    }

    @Override
    public String toString()
    {
        return id;
    }
}
