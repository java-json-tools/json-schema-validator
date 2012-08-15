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

package org.eel.kitchen.jsonschema.format;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eel.kitchen.jsonschema.util.NodeType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Specialized format validator for date/time checking
 *
 * <p><a href="http://joda-time.sourceforge.net/">Joda</a> is used for date and
 * time parsing, and more specifically
 * {@link DateTimeFormatter#parseDateTime(String)}: it can handle all defined
 * formats, and catches more errors than {@link SimpleDateFormat} does.</p>
 *
 * <p>What's more, unlike Java's {@link SimpleDateFormat},
 * Joda Time's {@link DateTimeFormatter} is thread-safe,
 * which is one more reason to use it.</p>
 */
public class AbstractDateFormatSpecifier
    extends FormatSpecifier
{
    /**
     * The error message in case of validation failure
     */
    private final String errmsg;

    /**
     * The {@link DateTimeFormatter}s to use
     */
    private final List<DateTimeFormatter> formats = new ArrayList<DateTimeFormatter>();

    /**
     * Constructor for just one format.
     *
     * @param fmt The date format
     * @param desc the description of the date format
     */
    protected AbstractDateFormatSpecifier(final String fmt, final String desc)
    {
        super(NodeType.STRING);
        formats.add(DateTimeFormat.forPattern(fmt));
        errmsg = String.format("string is not a valid %s", desc);
    }
    
    /**
     * Constructor for many formats, sharing a common description
     *
     * @param fmt The date formats
     * @param desc the description of the date formats
     */
    protected AbstractDateFormatSpecifier(final List<String> fmts, final String desc)
    {
        super(NodeType.STRING);
        for (String fmt : fmts){
            formats.add(DateTimeFormat.forPattern(fmt));
        }
        errmsg = String.format("string is not a valid %s", desc);
    }
    
    @Override
    final void checkValue(final List<String> messages, final JsonNode instance)
    {
        boolean hadSuccess = false;
        
        // If any of the supplied formats match, allow the data through.
        for (DateTimeFormatter dtf : formats){
            try {
                dtf.parseDateTime(instance.textValue());
                hadSuccess = true;
                break;
            } catch (IllegalArgumentException ignored) {
                // Ignore.
            }
        }
        
        if (!hadSuccess){
            messages.add(errmsg);
        }
    }
}
