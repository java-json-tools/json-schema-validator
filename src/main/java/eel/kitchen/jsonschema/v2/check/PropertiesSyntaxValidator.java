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

package eel.kitchen.jsonschema.v2.check;

//TODO: implement
public final class PropertiesSyntaxValidator
    extends UnsupportedSyntaxValidator
//    extends AbstractSyntaxValidator
{
    public PropertiesSyntaxValidator(final String fieldName)
    {
        super("properties");
    }

//    @Override
//    public boolean validate(final JsonNode schema)
//    {
//        final JsonNode node = schema.get("properties");
//
//        if (!node.isObject()) {
//            messages.add("illegal type for properties attribute (is "
//                + NodeType.getNodeType(node) + " , expected object");
//            return false;
//        }
//
//        boolean ret = true;
//
//        final Iterator<JsonNode> iterator = node.getElements();
//
//        while (iterator.hasNext())
//            ret = validateOne(iterator.next()) && ret;
//
//        return ret;
//    }

//    private boolean validateOne(final JsonNode element)
//    {
//        if (!element.isObject()) {
//            messages.add("non schema value in properties");
//            return false;
//        }
//
//        if (!element.has("required"))
//            return true;
//
//        if (element.get("required").isBoolean())
//            return true;
//
//        messages.add("required attribute of schema in properties is not a"
//            + " boolean");
//        return false;
//    }
}
