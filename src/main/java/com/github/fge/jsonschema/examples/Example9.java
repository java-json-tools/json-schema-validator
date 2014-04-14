/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.AbstractSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.digest.helpers.IdentityDigester;
import com.github.fge.jsonschema.keyword.digest.helpers.SimpleDigester;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.library.DraftV4Library;
import com.github.fge.jsonschema.library.Keyword;
import com.github.fge.jsonschema.library.KeywordBuilder;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.library.LibraryBuilder;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.messages.JsonSchemaValidationBundle;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.github.fge.msgsimple.source.MapMessageSource;
import com.github.fge.msgsimple.source.MessageSource;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * Ninth example: augmenting schemas with custom keywords
 *
 * <p><a href="doc-files/Example9.java">link to source code</a></p>
 *
 * <p><a href="doc-files/custom-keyword.json">link to schema</a></p>
 *
 * <p>This example adds a custom keyword with syntax checking, digesting and
 * keyword validation. The chosen keyword is {@code divisors}: it applies to
 * integer values and takes an array of (unique) integers as an argument.</p>
 *
 * <p>The validation is the same as for {@code multipleOf} except that it is
 * restricted to integer values and the instance must be a multiple of all
 * divisors. For instance, if the value of this keyword is {@code [2, 3]}, then
 * 6 validates successfully but 14 does not (it is divisible by 2 but not 3).
 * </p>
 *
 * <p>For this, you need to create your own keyword. This is done using {@link
 * Keyword#newBuilder(String)}, where the argument is the name of your keyword,
 * and then add the following elements:</p>
 *
 * <ul>
 *     <li>a {@link SyntaxChecker} (using {@link
 *     KeywordBuilder#withSyntaxChecker(SyntaxChecker)};</li>
 *     <li> a {@link Digester} (using {@link
 *     KeywordBuilder#withDigester(Digester)};</li>
 *     <li>and finally, a {@link KeywordValidator} (using {@link
 *     KeywordBuilder#withValidatorClass(Class)}.</li>
 * </ul>
 *
 * <p>Then, as in {@link Example8}, you need to get hold of a {@link Library}
 * (we choose again to extend the draft v4 library) and add the (frozen)
 * keyword to it using {@link LibraryBuilder#addKeyword(Keyword)}.</p>
 *
 * <p>The keyword validator <b>must</b> have a single constructor taking a
 * {@link JsonNode} as an argument (which will be the result of the {@link
 * Digester}). Note that you may omit to write a digester and choose instead to
 * use an {@link IdentityDigester} or a {@link SimpleDigester} (which you inject
 * into a keyword using {@link
 * KeywordBuilder#withIdentityDigester(NodeType, NodeType...)} and {@link
 * KeywordBuilder#withSimpleDigester(NodeType, NodeType...)} respectively).</p>
 *
 * <p>Two sample files are given: the first (<a
 * href="doc-files/custom-keyword-good.json">link</a>) is valid, the other (<a
 * href="doc-files/custom-keyword-bad.json">link</a>) isn't (the first and third
 * elements fail to divide by one or more factors).</p>
 */
public final class Example9
{
    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode customSchema = Utils.loadResource("/custom-keyword.json");
        final JsonNode good = Utils.loadResource("/custom-keyword-good.json");
        final JsonNode bad = Utils.loadResource("/custom-keyword-bad.json");

        /*
         * Build the new keyword
         */
        final Keyword keyword = Keyword.newBuilder("divisors")
            .withSyntaxChecker(DivisorsSyntaxChecker.getInstance())
            .withDigester(DivisorsDigesters.getInstance())
            .withValidatorClass(DivisorsKeywordValidator.class).freeze();

        /*
         * Build a library, based on the v4 library, with this new keyword
         */
        final Library library = DraftV4Library.get().thaw()
            .addKeyword(keyword).freeze();

        /*
         * Complement the validation message bundle with a dedicated message
         * for our keyword validator
         */
        final String key = "missingDivisors";
        final String value = "integer value is not a multiple of all divisors";
        final MessageSource source = MapMessageSource.newBuilder()
            .put(key, value).build();
        final MessageBundle bundle
            = MessageBundles.getBundle(JsonSchemaValidationBundle.class)
            .thaw().appendSource(source).freeze();

        /*
         * Build a custom validation configuration: add our custom library and
         * message bundle
         */
        final ValidationConfiguration cfg = ValidationConfiguration.newBuilder()
            .setDefaultLibrary("http://my.site/myschema#", library)
            .setValidationMessages(bundle).freeze();

        final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
            .setValidationConfiguration(cfg).freeze();

        final JsonSchema schema = factory.getJsonSchema(customSchema);

        ProcessingReport report;

        report = schema.validate(good);
        System.out.println(report);

        report = schema.validate(bad);
        System.out.println(report);
    }

    /*
     * Our custom syntax checker
     */
    private static final class DivisorsSyntaxChecker
        extends AbstractSyntaxChecker
    {
        private static final SyntaxChecker INSTANCE
            = new DivisorsSyntaxChecker();

        public static SyntaxChecker getInstance()
        {
            return INSTANCE;
        }

        private DivisorsSyntaxChecker()
        {
            /*
             * When constructing, the name for the keyword must be provided
             * along with the allowed type for the value (here, an array).
             */
            super("divisors", NodeType.ARRAY);
        }

        @Override
        protected void checkValue(final Collection<JsonPointer> pointers,
            final MessageBundle bundle, final ProcessingReport report,
            final SchemaTree tree)
            throws ProcessingException
        {
            /*
             * Using AbstractSyntaxChecker as a base, we know that when we reach
             * this method, the value has already been validated as being of
             * the allowed primitive types (only array here).
             *
             * But this is not enough for this particular validator: we must
             * also ensure that all elements of this array are integers. Cycle
             * through all elements of the array and check each element. If we
             * encounter a non integer argument, add a message.
             *
             * We must also check that there is at lease one element, that the
             * array contains no duplicates and that all elements are positive
             * integers and strictly greater than 0.
             *
             * The getNode() method grabs the value of this keyword for us, so
             * use that. Note that we also reuse some messages already defined
             * in SyntaxMessages.
             */
            final JsonNode node = getNode(tree);

            final int size = node.size();

            if (size == 0) {
                report.error(newMsg(tree, bundle, "emptyArray"));
                return;
            }

            NodeType type;
            JsonNode element;
            boolean uniqueItems = true;

            final Set<JsonNode> set = Sets.newHashSet();

            for (int index = 0; index < size; index++) {
                element = node.get(index);
                type = NodeType.getNodeType(element);
                if (type != NodeType.INTEGER)
                    report.error(newMsg(tree, bundle, "incorrectElementType")
                        .put("expected", NodeType.INTEGER)
                        .put("found", type));
                else if (element.bigIntegerValue().compareTo(BigInteger.ONE) < 0)
                    report.error(newMsg(tree, bundle, "integerIsNegative")
                        .put("value", element));
                uniqueItems = set.add(element);
            }

            if (!uniqueItems)
                report.error(newMsg(tree, bundle, "elementsNotUnique"));
        }
    }

    /*
     * Our custom digester
     *
     * We take the opportunity to build a digested form where, for instance,
     * [ 3, 5 ] and [ 5, 3 ] give the same digest.
     */
    private static final class DivisorsDigesters
        extends AbstractDigester
    {
        private static final Digester INSTANCE = new DivisorsDigesters();

        public static Digester getInstance()
        {
            return INSTANCE;
        }

        private DivisorsDigesters()
        {
            super("divisors", NodeType.INTEGER);
        }

        @Override
        public JsonNode digest(final JsonNode schema)
        {
            final SortedSet<JsonNode> set = Sets.newTreeSet(COMPARATOR);
            for (final JsonNode element: schema.get(keyword))
                set.add(element);

            return FACTORY.arrayNode().addAll(set);
        }

        /*
         * Custom Comparator. We compare BigInteger values, since all integers
         * are representable using this class.
         */
        private static final Comparator<JsonNode> COMPARATOR
            = new Comparator<JsonNode>()
        {
            @Override
            public int compare(final JsonNode o1, final JsonNode o2)
            {
                return o1.bigIntegerValue().compareTo(o2.bigIntegerValue());
            }
        };
    }


    /**
     * Custom keyword validator for {@link Example9}
     *
     * It must be {@code public} because it is built by reflection.
     */
    public static final class DivisorsKeywordValidator
        extends AbstractKeywordValidator
    {
        /*
         * We want to validate arbitrarily large integer values, we therefore
         * use BigInteger.
         */
        private final List<BigInteger> divisors;

        public DivisorsKeywordValidator(final JsonNode digest)
        {
            super("divisors");

            final ImmutableList.Builder<BigInteger> list
                = ImmutableList.builder();

            for (final JsonNode element: digest)
                list.add(element.bigIntegerValue());

            divisors = list.build();
        }

        @Override
        public void validate(final Processor<FullData, FullData> processor,
            final ProcessingReport report, final MessageBundle bundle,
            final FullData data)
            throws ProcessingException
        {
            final BigInteger value
                = data.getInstance().getNode().bigIntegerValue();
            /*
             * We use a plain list to store failed divisors: remember that the
             * digested form was built with divisors in order, we therefore
             * only need insertion order, and a plain ArrayList guarantees that.
             */
            final List<BigInteger> failed = Lists.newArrayList();

            for (final BigInteger divisor: divisors)
                if (!value.mod(divisor).equals(BigInteger.ZERO))
                    failed.add(divisor);

            if (failed.isEmpty())
                return;

            /*
             * There are missed divisors: report.
             */
            report.error(newMsg(data, bundle, "missingDivisors")
                .put("divisors", divisors).put("failed", failed));
        }

        @Override
        public String toString()
        {
            return "divisors: " + divisors;
        }
    }
}
