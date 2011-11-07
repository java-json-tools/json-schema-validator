/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
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

import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

import java.util.HashSet;
import java.util.Set;

/**
 * Validator for the {@code additionalProperties} keyword (draft section 5.4)
 */
public final class AdditionalPropertiesValidator
    extends SimpleKeywordValidator
{
    /**
     * Set to true if {@code additionalProperties} is either a schema or
     * {@code true}
     */
    private final boolean shortcut;

    /**
     * List of property names registered in the schema
     */
    private final Set<String> properties = new HashSet<String>();

    /**
     * List of patterns in {@code patternProperties}
     */
    private final Set<String> patterns = new HashSet<String>();

    public AdditionalPropertiesValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);

        shortcut = schema.get("additionalProperties").asBoolean(true);

        if (shortcut)
            return;

        JsonNode node;

        if (schema.has("properties")) {
            node = schema.get("properties");
            properties.addAll(CollectionUtils.toSet(node.getFieldNames()));
        }

        if (schema.has("patternProperties")) {
            node = schema.get("patternProperties");
            patterns.addAll(CollectionUtils.toSet(node.getFieldNames()));
        }
    }

    /**
     * <p>Validate the {@code additionalProperties} keyword:</p>
     * <ul>
     *     <li>if it is a schema, or {@code true}, the validation succeeds;
     *     </li>
     *     <li>else, try and see if the instance field is either registered
     *     in {@link #properties} or matches at least one regex in {@link
     *     #patterns}. If none of this is true, the validation fails.
     *     </li>
     * </ul>
     */
    @Override
    protected void validateInstance()
    {
        if (shortcut)
            return;

        final Set<String> fields =
            CollectionUtils.toSet(instance.getFieldNames());

        fields.removeAll(properties);

        if (fields.isEmpty())
            return;

        for (final String field: fields) {
            if (patternsMatch(field))
                continue;
            report.addMessage("additional properties are not permitted");
            return;
        }
    }

    /**
     * Attempt to match an instance field name against {@link #patterns}
     *
     * @param field the field to match
     * @return true on a match
     * @see {@link RhinoHelper#regMatch(String, String)}
     */
    private boolean patternsMatch(final String field)
    {
        for (final String regex: patterns)
            if (RhinoHelper.regMatch(regex, field))
                return true;

        return false;
    }
}
