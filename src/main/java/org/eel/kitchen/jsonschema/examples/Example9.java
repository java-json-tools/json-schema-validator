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

package org.eel.kitchen.jsonschema.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.main.Keyword;
import org.eel.kitchen.jsonschema.metaschema.KeywordRegistries;
import org.eel.kitchen.jsonschema.metaschema.KeywordRegistry;
import org.eel.kitchen.jsonschema.metaschema.SchemaURIs;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.AbstractSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Ninth example: augmenting schemas with custom keywords
 *
 * <p><a href="doc-files/Example9.java">link to source code</a></p>
 *
 * <p><a href="doc-files/custom-keyword.json">link to schema</a></p>
 *
 * <p>This example adds a custom keyword with syntax checking and keyword
 * validation. The chosen keyword is {@code divisors}: it applies to integer
 * values and takes an array of (unique) integers as an argument. The
 * validation is the same as for {@code divisibleBy} (or {@code multipleOf}),
 * except that the result must be zero for all divisors. For instance, if the
 * value of this keyword is {@code [2, 3]}, then 6 validates successfully but
 * 14 does not (it is divisible by 2 but not 3).</p>
 *
 * <p>The principle is the same as adding format attributes (see {@link
 * Example8}), with the difference that the keyword is built using the {@link
 * Keyword} class. You need three elements to add a custom keyword:</p>
 *
 * <ul>
 *     <li>its name (obviously enough),</li>
 *     <li>a {@link SyntaxChecker} (optional),</li>
 *     <li>a {@link KeywordValidator} (optional).</li>
 * </ul>
 *
 * <p>Even though you may omit syntax validation, it is not recommended: this
 * means you would need to do type argument checking in the keyword validator
 * constructor. It is all the less recommended that all keyword validators are
 * built using reflection.</p>
 *
 * <p>The keyword validator <b>must</b> have a single constructor taking a
 * {@link JsonNode} as an argument (this will be the schema).</p>
 *
 * <p>Unlike for {@link Example8}, here we choose to augment draft v4 instead
 * of draft v3, and not making it the default (which means the schema must
 * have a {@code $schema} member with the appropriate value).</p>
 *
 * <p>Two sample files are given: the first (<a
 * href="doc-files/custom-keyword-good.json">link</a>) is valid, the other (<a
 * href="doc-files/custom-keyword-bad.json">link</a>) isn't (the first and third
 * elements fail to divide by one or more factors).</p>
 */
public final class Example9
    extends ExampleBase
{
    public static void main(final String... args)
        throws IOException
    {
        final JsonNode customSchema = loadResource("/custom-keyword.json");
        final JsonNode good = loadResource("/custom-keyword-good.json");
        final JsonNode bad = loadResource("/custom-keyword-bad.json");

        final JsonRef ref = SchemaURIs.draftV4Core();
        final KeywordRegistry registry = KeywordRegistries.draftV4Core();

        final Keyword keyword = Keyword.withName("divisors")
            .withSyntaxChecker(DivisorsSyntaxChecker.getInstance())
            .withValidatorClass(DivisorsKeywordValidator.class)
            .build();

        registry.addKeyword(keyword);

        final JsonSchemaFactory factory = new JsonSchemaFactory.Builder()
            .addKeywordRegistry(ref, registry, false).build();

        final JsonSchema schema = factory.fromSchema(customSchema);

        ValidationReport report;

        report = schema.validate(good);
        printReport(report);

        report = schema.validate(bad);
        printReport(report);
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
        public void checkValue(final Message.Builder msg,
            final List<Message> messages, final JsonNode schema)
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
             * We must also check that there is at lease one element, and that
             * the array contains no duplicates.
             */
            final JsonNode node = schema.get(keyword);
            final int size = node.size();

            if (size == 0) {
                msg.setMessage("array must have at least one element");
                messages.add(msg.build());
                return;
            }

            NodeType type;
            JsonNode element;

            final Set<JsonNode> set = Sets.newHashSet();

            for (int index = 0; index < size; index++) {
                element = node.get(index);
                type = NodeType.getNodeType(element);
                if (!set.add(element)) {
                    msg.clearInfo().setMessage("duplicate elements in array");
                    messages.add(msg.build());
                    return;
                }
                if (type != NodeType.INTEGER) {
                    msg.setMessage("array element has incorrect type")
                        .addInfo("expected", NodeType.INTEGER)
                        .addInfo("index", index);
                    messages.add(msg.build());
                }
            }
        }
    }

    /**
     * Custom keyword validator for {@link Example9}
     *
     * It must be {@code public} because it is built by reflection.
     */
    public static final class DivisorsKeywordValidator
        extends KeywordValidator
    {
        /*
         * We want to validate arbitrarily large integer values, we therefore
         * use BigInteger.
         */
        private final Set<BigInteger> divisors;

        public DivisorsKeywordValidator(final JsonNode schema)
        {
            super("divisors", NodeType.INTEGER);

            /*
             * Use Google's ImmutableSet
             */
            final ImmutableSet.Builder<BigInteger> set = ImmutableSet.builder();

            for (final JsonNode element: schema.get(keyword))
                set.add(element.bigIntegerValue());

            divisors = set.build();
        }

        @Override
        public void validate(final ValidationContext context,
            final ValidationReport report, final JsonNode instance)
        {
            final BigInteger value = instance.bigIntegerValue();
            final Set<BigInteger> failed = Sets.newHashSet();

            for (final BigInteger divisor: divisors)
                if (!value.mod(divisor).equals(BigInteger.ZERO))
                    failed.add(divisor);

            if (failed.isEmpty())
                return;

            /*
             * There are missed divisors: report.
             *
             * For nicer report, order the divisors using Google's Ordering.
             */
            final Message msg = newMsg()
                .setMessage("integer value is not a multiple of all divisors")
                .addInfo("divisors", Ordering.natural().sortedCopy(divisors))
                .addInfo("failed", Ordering.natural().sortedCopy(failed))
                .build();

            report.addMessage(msg);
        }

        @Override
        public String toString()
        {
            return "divisors: " + Ordering.natural().sortedCopy(divisors);
        }
    }
}
