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
package org.eel.kitchen.jsonschema.context;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.JsonValidator;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.factories.KeywordFactory;
import org.eel.kitchen.jsonschema.factories.SyntaxFactory;
import org.eel.kitchen.jsonschema.keyword.RefKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.JsonLoader;
import org.eel.kitchen.util.NodeType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <p>Class passed to all {@link Validator} implementations. This class is
 * responsible for several things:</p>
 * <ul>
 *     <li>checking the schema correctness (using {@link SyntaxValidator}
 *     instances);</li>
 *     <li>create validator instances;</li>
 *     <li>resolve {@code $ref} (see {@link RefKeywordValidator}) <b>and</b>
 *     detect ref looping;</li>
 *     <li>provide {@link ValidationReport} instances;</li>
 *     <li>provide other instances of itself.</li>
 * </ul>
 */
public final class ValidationContext
{
    /**
     * Pattern to split JSON Path components
     */
    private static final Pattern SPLIT_PATTERN = Pattern.compile("/");

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

    private final Map<URI, JsonNode> locators = new HashMap<URI, JsonNode>();

    /**
     * The ref result lookups for this {@link #path},
     * used for ref looping detection (see {@link #resolveRef(String)}
     */
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
        refLookups = new HashSet<JsonNode>();
    }

    /**
     * Unregister all validators ({@link SyntaxValidator} and
     * {@link KeywordValidator} for a given keyword. Note that the null case
     * is handled in the factories themselves.
     *
     * @param keyword the victim
     */
    public void unregisterValidator(final String keyword)
    {
        syntaxFactory.unregisterValidator(keyword);
        keywordFactory.unregisterValidator(keyword);
    }

    /**
     * Register a validator for a new keyword, or replace the existing
     * validators by new ones for ths keyword.
     *
     * @param keyword the new/modified keyword
     * @param sv the {@link SyntaxValidator} implementation
     * @param kv the {@link KeywordValidator} implementation
     * @param types the list of JSON types the keyword validator is able to
     * validate
     * @throws IllegalArgumentException one validator is null,
     * or the type list is empty
     */
    public void registerValidator(final String keyword,
        final Class<? extends SyntaxValidator> sv,
        final Class<? extends KeywordValidator> kv, final NodeType... types)
    {
        if (sv == null)
            throw new IllegalArgumentException("syntax validator is null");
        syntaxFactory.registerValidator(keyword, sv);

        if (kv == null)
            throw new IllegalArgumentException("keyword validator is null");

        if (types.length == 0)
            throw new IllegalArgumentException("validator wouldn't match any "
                + "JSON instance (empty type set)");

        keywordFactory.registerValidator(keyword, kv, types);
    }

    /**
>>>>>>> ValidationContext: implement .registerValidator
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
        other.refLookups = new HashSet<JsonNode>();
        if (newPath.equals(path))
            other.refLookups.addAll(refLookups);
        other.locators.putAll(locators);
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
     * <p>Given a JSON Schema reference, return a context for this reference.
     * The context will be spawned with a different {@link #rootSchema} if
     * the {@code ref} argument is an absolute URI.</p>
     *
     * <p>This method is only used by {@link RefKeywordValidator} currently
     * .</p>
     *
     * @param ref the reference to resolve
     * @return the matching context
     * @throws IOException the ref is invalid or unsupported
     */
    public ValidationContext resolveRef(final String ref)
        throws IOException
    {
        final URI fullURI;
        final URI baseURI;
        final String jsonPath;

        try {
            fullURI = new URI(ref).normalize();
            jsonPath = fullURI.getFragment();
            baseURI = new URI(fullURI.getScheme(),
                fullURI.getSchemeSpecificPart(), null);
        } catch (URISyntaxException e) {
            throw new IOException("How did I get there?? The URI should "
                + "have been validated already!", e);
        }

        JsonNode schema = rootSchema;

        final boolean absolute = baseURI.isAbsolute();

        if (absolute) {
            if (!"http".equals(baseURI.getScheme()))
                throw new IOException("sorry, only HTTP is supported as a "
                    + "scheme currently");
            if (!locators.containsKey(baseURI)) {
                schema = JsonLoader.fromURL(baseURI.toURL());
                locators.put(baseURI, schema);
            } else
                schema = locators.get(baseURI);
        } else if (!"".equals(baseURI.getSchemeSpecificPart()))
            throw new IOException("non empty scheme specific part in "
                + "non absolute URI");

        final JsonNode node = resolvePath(schema, jsonPath);

        final ValidationContext ret = createContext(node);

        if (absolute)
            ret.rootSchema = schema;

        return ret;
    }

    /**
     * <p>Given a schema and a JSON Path, return the corresponding entry in
     * the schema. Error out on either of the following:</p>
     * <ul>
     *     <li>the path does not exist, or</li>
     *     <li>a loop is detected (by looking up in {@link #refLookups} if
     *     the result has already been seen for this {@link
     *     #rootSchema} and {@link #path}).
     *     </li>
     * </ul>
     *
     * @param schema the schema
     * @param jsonPath the JSON path (<i>without</i> the initial {@code #}
     * @return the entry
     * @throws IOException see description
     */
    private JsonNode resolvePath(final JsonNode schema, final String jsonPath)
        throws IOException
    {
        if (jsonPath == null)
            return schema;

        JsonNode ret = schema;

        for (final String pathElement: SPLIT_PATTERN.split(jsonPath)) {
            if (pathElement.isEmpty())
                continue;
            ret = schema.path(pathElement);
        }

        if (ret.isMissingNode())
            throw new IOException("non existent path #" + jsonPath + " in "
                + "schema");

        if (refLookups.contains(ret))
            throw new IOException(ret + " loops on itself");

        refLookups.add(ret);
        return ret;
    }
}
