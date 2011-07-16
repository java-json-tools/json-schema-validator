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

package eel.kitchen.jsonschema.validators.misc;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Collection;
import java.util.HashSet;

/**
 * Validator for the "enum" keyword (section 5.19). This validator is
 * completely type agnostic.
 */
public final class EnumValidator
    extends AbstractValidator
{
    /**
     * The set of values found in the enumeration
     */
    private final Collection<JsonNode> values = new HashSet<JsonNode>();

    /**
     * Set to true if no enum was found - in which case the validation always
     * succeeds
     */
    private boolean voidEnum = false;

    public EnumValidator()
    {
        registerField("enum", NodeType.ARRAY);
    }

    /**
     * Fill the <code>values</code> set, if necessary. Duplicates are
     * ignored, as the spec does not mention that values in an enum should be
     * unique.
     *
     * @return always true
     */
    @Override
    protected boolean doSetup()
    {
        final JsonNode node = schema.get("enum");

        if (node != null)
            values.addAll(CollectionUtils.toSet(node.iterator()));
        else
            voidEnum = true;

        return true;
    }

    /**
     * <p>Validates against the enum, if one is present,
     * otherwise checks whether one of the members in the enumeration
     * strictly equals to the instance.</p>
     *
     * <p>This is really simple since {@link JsonNode}'s .equals() does the
     * job for us.</p>
     *
     * @param node the instance to validate
     * @return false iff an enum is present and the instance matches none of
     * the elements
     */
    @Override
    protected boolean doValidate(final JsonNode node)
    {
        if (voidEnum)
            return true;
        if (values.contains(node))
            return true;

        messages.add("node does not match any value in the enumeration");
        return false;
    }
}
