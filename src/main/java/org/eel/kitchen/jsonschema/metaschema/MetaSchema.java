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

package org.eel.kitchen.jsonschema.metaschema;

import org.eel.kitchen.jsonschema.format.FormatAttribute;
import org.eel.kitchen.jsonschema.keyword.KeywordFactory;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;

import java.util.Map;

public final class MetaSchema
{
    private final JsonRef schemaURI;

    private final SyntaxValidator syntaxValidator;

    private final KeywordFactory keywordFactory;

    private final Map<String, FormatAttribute> formatAttributes;

    public MetaSchema(final JsonRef schemaURI, final KeywordRegistry registry)
    {
        this.schemaURI = schemaURI;
        syntaxValidator = new SyntaxValidator(registry.getSyntaxCheckers());
        keywordFactory = new KeywordFactory(registry.getValidators());
        formatAttributes = registry.getFormatAttributes();
    }

    public JsonRef getSchemaURI()
    {
        return schemaURI;
    }

    public SyntaxValidator getSyntaxValidator()
    {
        return syntaxValidator;
    }

    public KeywordFactory getKeywordFactory()
    {
        return keywordFactory;
    }

    public Map<String, FormatAttribute> getFormatAttributes()
    {
        // This is OK: the returned map is immutable
        return formatAttributes;
    }
}
