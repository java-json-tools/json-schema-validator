/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.bundle;

import org.eel.kitchen.jsonschema.keyword.PropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.syntax.PropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.RequiredSyntaxValidator;
import org.eel.kitchen.util.NodeType;

public final class DraftV3ValidatorBundle
    extends CommonValidatorBundle
{
    public DraftV3ValidatorBundle()
    {
        registerSV("required", new RequiredSyntaxValidator());
        registerIgnoredKV("required", NodeType.OBJECT);

        registerSV("properties", new PropertiesSyntaxValidator());
        registerKV("properties", new PropertiesKeywordValidator(),
            NodeType.OBJECT);
    }
}
