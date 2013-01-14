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
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.AbstractSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.List;
import java.util.Set;

/**
 * Hyper-schema {@code fragmentResolution} syntax checker
 *
 * <p>Easy enough, it can only take two values.</p>
 */
public final class FragmentResolutionSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final SyntaxChecker INSTANCE
        = new FragmentResolutionSyntaxChecker();

    private static final Set<String> PROTOCOLS
        = ImmutableSet.of("slash-delimited", "dot-delimited");

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private FragmentResolutionSyntaxChecker()
    {
        super("fragmentResolution", NodeType.STRING);
    }

    @Override
    public void checkValue(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema)
    {
        final String protocol = schema.get(keyword).textValue();

        if (PROTOCOLS.contains(protocol))
            return;

        messages.add(newMsg().addInfo("possible-values", PROTOCOLS)
            .setMessage("unknown fragment resolution protocol")
            .addInfo("found", protocol).build());
    }
}
