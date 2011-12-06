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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.bundle;

import org.eel.kitchen.jsonschema.keyword.draftv3.PropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.syntax.draftv3.DollarRefSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.draftv3.PropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.draftv3.RequiredSyntaxValidator;
import org.eel.kitchen.util.NodeType;

/**
 * Validator bundle for JSON Schema draft v3
 */
public final class DraftV3ValidatorBundle
    extends BuiltinValidatorBundle
{
    private static final ValidatorBundle instance
        = new DraftV3ValidatorBundle();

    public static ValidatorBundle getInstance()
    {
        return instance;
    }

    private DraftV3ValidatorBundle()
    {
        /* properties */
        registerSV("properties", PropertiesSyntaxValidator.getInstance());
        registerKV("properties", PropertiesKeywordValidator.getInstance(),
            NodeType.OBJECT);

        /* required */
        registerSV("required", RequiredSyntaxValidator.getInstance());

        /* $ref -- syntax only */
        registerSV("$ref", DollarRefSyntaxValidator.getInstance());
    }
}
