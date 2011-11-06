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

package eel.kitchen.jsonschema.context;

import eel.kitchen.jsonschema.JsonValidator;
import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.factories.KeywordFactory;
import eel.kitchen.jsonschema.factories.SyntaxFactory;
import eel.kitchen.jsonschema.keyword.RefValidator;
import eel.kitchen.jsonschema.syntax.SyntaxValidator;
import eel.kitchen.util.RefResolver;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.MissingNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Class passed to all {@link Validator} implementations. This class is
 * responsible for several things:</p>
 * <ul>
 *     <li>checking the schema correctness (using {@link SyntaxValidator}
 *     instances);</li>
 *     <li>create validator instances;</li>
 *     <li>resolve {@code $ref} (see {@link RefValidator});</li>
 *     <li>provide {@link ValidationReport} instances;</li>
 *     <li>provide other instances of itself.</li>
 * </ul>
 */
public final class ValidationContext
{
    /**
     * The root schema of this validation context
     */
    private JsonNode rootSchema;

    /**
     * The schema used by the current context
     */
    private JsonNode schemaNode;

    /**
     * The JSON path within the instance for the current context
     */
    private String path;

    /**
     * The keyword validator factory
     */
    private KeywordFactory keywordFactory;

    /**
     * The syntax validator factory
     */
    private SyntaxFactory syntaxFactory;

    /**
     * The $ref resolver
     */
    private RefResolver refResolver;

    private Set<JsonNode> refLookups;
    /**
     * The default constructor, which is private by design
     */
    private ValidationContext()
    {
    }

    /**
     * The public constructor. Only used from {@link JsonValidator}. On
     * initial setup, the argument is the root schema, see #rootSchema.
     *
     * @param schemaNode the root schema used by this context
     */
    public ValidationContext(final JsonNode schemaNode)
    {
        path = "#";
        rootSchema = this.schemaNode = schemaNode;

        keywordFactory = new KeywordFactory();
        syntaxFactory = new SyntaxFactory();
        refResolver = new RefResolver(schemaNode);
        refLookups = new HashSet<JsonNode>();
    }

    /**
     * Return the schema node of this context -- <b>not</b> the root schema!
     *
     * @return the matching {@link JsonNode}
     */
    public JsonNode getSchemaNode()
    {
        return schemaNode;
    }

    /**
     * Spawn a new context from this context.
     *
     * @param subPath the relative path to use from the current #path
     * @param subSchema the schema node to use for this context
     * @return the new context
     */
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
        other.refResolver = refResolver;
        other.refLookups = new HashSet<JsonNode>();
        if (newPath.equals(path))
            other.refLookups.addAll(refLookups);
        return other;
    }

    /**
     * Shortcut to call {@link #createContext(String, JsonNode)}
     * with an empty path
     *
     * @param subSchema the schema node to use
     * @return the new context
     */
    public ValidationContext createContext(final JsonNode subSchema)
    {
        return createContext("", subSchema);
    }

    /**
     * Create a {@link Validator} for a given JSON instance. This is what
     * MUST be called by validators when they need to spawn a new validator,
     * because this method handles syntax checking. If the syntax of the
     * schema itself is wrong, returns an {@link AlwaysFalseValidator}.
     *
     * @param instance the JSON instance
     * @return the validator
     */
    public Validator getValidator(final JsonNode instance)
    {
        final ValidationReport report = new ValidationReport(path);

        final Validator v = syntaxFactory.getValidator(this);

        report.mergeWith(v.validate());

        if (!report.isSuccess())
            return new AlwaysFalseValidator(report);

        return keywordFactory.getValidator(this, instance);
    }

    /**
     * Create a new report with, optionally, a prefix (this DOES NOT affect
     * the path)
     *
     * @param prefix the prefix to use
     * @return the newly created report
     */
    public ValidationReport createReport(final String prefix)
    {
        return new ValidationReport(path + prefix);
    }

    /**
     * Shortcut to {@link #createReport(String)} with an empty prefix
     *
     * @return the newly created report
     */
    public ValidationReport createReport()
    {
        return createReport("");
    }

    /**
     * Resolve a {@code $ref} relatively to #rootSchema. Used by {@link
     * RefValidator}.
     *
     * @param path the path within the root schema
     * @return the {@link JsonNode}, which is a {@link MissingNode} if the
     * path does not exist
     * @throws IOException the ref points to an URL, and the JSON schema at
     * this URL could not be downloaded
     */
    public JsonNode resolve(final String path)
        throws IOException
    {
        final JsonNode node = refResolver.resolve(path);

        if (refLookups.contains(node))
            throw new IOException(node + " loops on itself");

        refLookups.add(node);
        return node;
    }
}
