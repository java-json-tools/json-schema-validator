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
public final class PatternPropertiesSyntaxValidator
    extends UnsupportedSyntaxValidator
//    extends AbstractSyntaxValidator
{
    public PatternPropertiesSyntaxValidator()
    {
        super("patternProperties");
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
//        final Map<String, JsonNode> map
//            = CollectionUtils.toMap(node.getFields());
//
//        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
//            if (!RhinoHelper.regexIsValid(entry.getKey())) {
//                messages.add("patternProperties: regex " + entry.getKey()
//                    + " is invalid");
//                ret = false;
//            }
//            if (!entry.getValue().isObject()) {
//                messages.add("non schema value in patternProperties");
//                ret = false;
//            }
//        }
//        return ret;
//    }
}
