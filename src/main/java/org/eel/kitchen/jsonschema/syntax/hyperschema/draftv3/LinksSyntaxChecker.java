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

package org.eel.kitchen.jsonschema.syntax.hyperschema.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.AbstractSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.CharMatchers;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Pattern;

/**
 * Syntax validator for hyper shema's {@code links} keyword
 *
 * <p>This keyword is quite the monster, and what is more, the JSON file does
 * not agree with the spec on all points. It is chosen here to follow the
 * specification.</p>
 */
public final class LinksSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final Set<String> LDO_REQUIRED_MEMBERS
        = ImmutableSet.of("href", "rel");

    // FIXME: does not account for { or } within templates
    private static final Pattern HREF_TEMPLATE
        = Pattern.compile("\\{[^{}]*\\}");

    private static final SyntaxChecker INSTANCE = new LinksSyntaxChecker();

    private LinksSyntaxChecker()
    {
        super("links", NodeType.ARRAY);
    }

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void checkValue(final Message.Builder msg,
        final List<Message> messages, final JsonNode schema)
    {
        final JsonNode value = schema.get(keyword);
        final int size = value.size();

        JsonNode ldo;
        NodeType type;

        for (int index = 0; index < size; index++) {
            ldo = value.get(index);
            type = NodeType.getNodeType(ldo);
            msg.addInfo("index", index);
            if (type != NodeType.OBJECT) {
                msg.addInfo("expected", NodeType.OBJECT).addInfo("found", type)
                    .setMessage("incorrect array element type");
                messages.add(msg.build());
                continue;
            }
            checkLDOSyntax(msg, messages, ldo);
        }
    }

    private static void checkLDOSyntax(final Message.Builder msg,
        final List<Message> messages, final JsonNode ldo)
    {
        final Set<String> memberNames = Sets.newHashSet(ldo.fieldNames());

        if (!memberNames.containsAll(LDO_REQUIRED_MEMBERS)) {
            final SortedSet<String> missing
                = Sets.newTreeSet(LDO_REQUIRED_MEMBERS);

            missing.removeAll(memberNames);

            msg.addInfo("required", LDO_REQUIRED_MEMBERS)
                .addInfo("missing", missing)
                .setMessage("missing required properties in link description "
                    + "object");
            messages.add(msg.build());
            return;
        }

        /*
         * Check correctness of the "rel" and "href" members
         */

        checkRelation(msg, messages, ldo);
        checkHref(msg, messages, ldo);

        if (ldo.has("targetSchema")) {
            final NodeType type = NodeType.getNodeType(ldo.get("targetSchema"));
            if (type != NodeType.OBJECT) {
                msg.setMessage("incorrect type for targetSchema member")
                    .addInfo("expected", NodeType.OBJECT)
                    .addInfo("found", type);
                messages.add(msg.build());
            }
        }

        /*
         * Check submission links properties (section 6.1.1.4 of the draft)
         */
        checkSubmissionLink(msg, messages, ldo);
    }

    private static void checkRelation(final Message.Builder msg,
        final List<Message> messages, final JsonNode ldo)
    {
        /*
         * FIXME: whether it describes a real relation is actually not checked.
         *
         * We check that the syntax of the relation matches the "reg-rel-type"
         * grammar token defined in RFC 5988, section 5.
         */
        final JsonNode relNode = ldo.get("rel");
        final NodeType type = NodeType.getNodeType(relNode);

        if (type != NodeType.STRING) {
            msg.setMessage("wrong node type for \"rel\" member")
                .addInfo("expected", NodeType.STRING).addInfo("found", type);
            messages.add(msg.build());
            return;
        }

        final String relation = relNode.textValue();

        if (relation.isEmpty()) {
            msg.setMessage("\"rel\" member value must not be empty");
            messages.add(msg.build());
            return;
        }

        if (!CharMatchers.LOALPHA.matches(relation.charAt(0))) {
            msg.setMessage("illegal \"rel\" member value: must start with a "
                + "lowercase ASCII letter");
            messages.add(msg.build());
            return;
        }

        if (!CharMatchers.REL_TOKEN.matchesAllOf(relation.substring(1))) {
            msg.setMessage("illegal token in \"rel\" member value");
            messages.add(msg.build());
        }
    }

    private static void checkHref(final Message.Builder msg,
        final List<Message> messages, final JsonNode ldo)
    {
        final JsonNode hrefNode = ldo.get("href");
        final NodeType type = NodeType.getNodeType(hrefNode);

        if (type != NodeType.STRING) {
            msg.setMessage("wrong node type for \"href\" member")
                .addInfo("expected", NodeType.STRING).addInfo("found", type);
            messages.add(msg.build());
            return;
        }

        // We must expand templates with a value which is valid in all places
        // of a URI. ASCII lowercase letters are always valid, use that.
        final String template = hrefNode.textValue();
        final String uri = HREF_TEMPLATE.matcher(template).replaceAll("foo");

        try {
            new URI(uri);
        } catch (URISyntaxException ignored) {
            msg.setMessage("expanded href value is not a URI")
                .addInfo("template", template);
            messages.add(msg.build());
        }
    }

    private static void checkSubmissionLink(final Message.Builder msg,
        final List<Message> messages, final JsonNode ldo)
    {
        /*
         * This is quite messy: in the hyper-schema document, there is no
         * "schema", however, in the draft text, there is... Stick to the draft.
         *
         * And there is also "targetSchema" along with "schema"... Gee.
         */
        NodeType type;

        if (ldo.has("schema")) {
            type = NodeType.getNodeType(ldo.get("schema"));
            if (type != NodeType.OBJECT) {
                msg.setMessage("incorrect type for schema member")
                    .addInfo("expected", NodeType.OBJECT)
                    .addInfo("found", type);
                messages.add(msg.build());
            }
        }

        if (!ldo.has("enctype"))
            // Nothing to do
            return;

        if (!ldo.has("method")) {
            msg.setMessage("enctype must be paired with method");
            messages.add(msg.build());
            return;
        }

        JsonNode node;

        /*
         * Check enctype
         */
        node = ldo.get("enctype");
        type = NodeType.getNodeType(node);

        if (type != NodeType.STRING) {
            msg.setMessage("incorrect type for enctype").addInfo("found", type)
                .addInfo("expected", NodeType.STRING);
            messages.add(msg.build());
        } else
            try {
                MediaType.parse(node.textValue());
            } catch (IllegalArgumentException ignored) {
                msg.setMessage("enctype is not a valid media type")
                    .addInfo("value", node);
                messages.add(msg.build());
            }

        /*
         * Check method
         */

        node = ldo.get("method");
        type = NodeType.getNodeType(node);
        if (type != NodeType.STRING) {
            msg.setMessage("incorrect type for method").addInfo("found", type)
                .addInfo("expected", NodeType.STRING);
            messages.add(msg.build());
        }
    }
}
