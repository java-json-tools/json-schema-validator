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

package com.github.fge.jsonschema.syntax.hyperschema.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.syntax.AbstractSyntaxChecker;
import com.github.fge.jsonschema.syntax.SyntaxChecker;
import com.github.fge.jsonschema.syntax.SyntaxValidator;
import com.github.fge.jsonschema.util.CharMatchers;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

/**
 * Tentative syntax checker for the {@code contentEncoding} hyperschema keyword
 *
 * <p>Draft v3 refers to RFC 2045, section 6.1. Unfortunately, this leaves quite
 * some room for interpretation for what is called an "ietf-token".</p>
 *
 * <p>We choose to not include such tokens here. We stick with the already
 * defined types and the {@code x-token} definitions.</p>
 */
public final class ContentEncodingSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final SyntaxChecker INSTANCE
        = new ContentEncodingSyntaxChecker();

    private static final Set<String> ENCODINGS = ImmutableSet.of("7bit", "8bit",
        "binary", "quoted-printable", "base64");

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private ContentEncodingSyntaxChecker()
    {
        super("contentEncoding", NodeType.STRING);
    }

    @Override
    public void checkValue(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema)
    {
        /*
         * RFC 2045 explicitly says that the value for content encoding is case
         * insensitive, account for it
         */
        final String value = schema.get(keyword).textValue();
        final String protocol = value.toLowerCase();

        /*
         * If the value is a well-known value, account for it
         */
        if (ENCODINGS.contains(protocol))
            return;

        /*
         * If the value starts with "x-" (case insensitive), then what follows
         * that "x-" must be what RFC 2045 defines as a token. Corollary: if it
         * does not start with x, it is illegal.
         */

        final Message.Builder msg = newMsg().addInfo("value", value);

        if (!protocol.startsWith("x-")) {
            msg.setMessage("illegal content encoding");
            messages.add(msg.build());
            return;
        }

        final String s = protocol.substring(2);

        if (s.isEmpty()) {
            msg.setMessage("illegal content encoding: empty string after "
                + "\"x-\"");
            messages.add(msg.build());
            return;
        }

        if (!CharMatchers.RFC2045_TOKEN.matchesAllOf(s)) {
            msg.setMessage("illegal content encoding: illegal token in custom"
                + " content encoding");
            messages.add(msg.build());
        }
    }
}
