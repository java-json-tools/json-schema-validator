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

import org.eel.kitchen.jsonschema.keyword.draftv4.MaxPropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.MinPropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.RequiredKeywordValidator;
import org.eel.kitchen.jsonschema.syntax.draftv4.DollarRefSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.draftv4.MaxPropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.draftv4.MinPropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.draftv4.PropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.draftv4.RequiredSyntaxValidator;
import org.eel.kitchen.util.NodeType;

/**
 * Validator bundle for JSON Schema draft v4
 *
 * <p><b>EXPERIMENTAL!</b> Draft 4 is not even out yet,
 * but the keywords (re)defined by this bundle have a very high chance of
 * making it into the finished draft.</p>
 */
public final class DraftV4ValidatorBundle
    extends BuiltinValidatorBundle
{
    private static final ValidatorBundle instance
        = new DraftV4ValidatorBundle();

    public static ValidatorBundle getInstance()
    {
        return instance;
    }

    private DraftV4ValidatorBundle()
    {
        /* maxProperties */
        registerSV("maxProperties", MaxPropertiesSyntaxValidator.getInstance());
        registerKV("maxProperties", MaxPropertiesKeywordValidator.getInstance(),
            NodeType.OBJECT);

        /* minProperties */
        registerSV("minProperties", MinPropertiesSyntaxValidator.getInstance());
        registerKV("minProperties", MinPropertiesKeywordValidator.getInstance(),
            NodeType.OBJECT);

        /* required */
        registerSV("required", RequiredSyntaxValidator.getInstance());
        registerKV("required", RequiredKeywordValidator.getInstance(),
            NodeType.OBJECT);

        /* properties */
        registerSV("properties", PropertiesSyntaxValidator.getInstance());

        /* $ref -- syntax only */
        registerSV("$ref", DollarRefSyntaxValidator.getInstance());
    }
}
