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

package eel.kitchen.jsonschema.context;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.factories.KeywordFactory;
import eel.kitchen.jsonschema.factories.SyntaxFactory;
import org.codehaus.jackson.JsonNode;

public final class ValidationContext
{
    private JsonNode rootSchema;
    private JsonNode schemaNode;
    private String path;

    private KeywordFactory keywordFactory;
    private SyntaxFactory syntaxFactory;

    private ValidationContext()
    {
    }

    public ValidationContext(final JsonNode schemaNode)
    {
        path = "#";
        rootSchema = this.schemaNode = schemaNode;

        keywordFactory = new KeywordFactory();
        syntaxFactory = new SyntaxFactory();
    }

    public KeywordFactory getKeywordFactory()
    {
        return keywordFactory;
    }

    public JsonNode getSchemaNode()
    {
        return schemaNode;
    }

    public ValidationContext createContext(final String subPath,
        final JsonNode subSchema)
    {
        final String newPath = subPath == null || subPath.isEmpty()
            ? path
            : String.format("%s/%s", path, subPath);

        final ValidationContext other = new ValidationContext();
        other.path = newPath;
        other.rootSchema = rootSchema;
        other.schemaNode = subSchema;
        other.keywordFactory = keywordFactory;
        other.syntaxFactory = syntaxFactory;
        return other;
    }

    public ValidationContext createContext(final JsonNode subSchema)
    {
        return createContext("", subSchema);
    }

    public Validator getValidator(final JsonNode instance)
    {
        final ValidationReport report = new ValidationReport(path);

        final Validator v = syntaxFactory.getValidator(this);

        report.mergeWith(v.validate());

        if (!report.isSuccess())
            return new AlwaysFalseValidator(report);

        return keywordFactory.getValidator(this, instance);
    }

    public ValidationReport createReport()
    {
        return createReport("");
    }

    public ValidationReport createReport(final String prefix)
    {
        return new ValidationReport(path + prefix);
    }
}
