/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.format.draftv3;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import static com.github.fge.jsonschema.messages.FormatMessages.*;

/**
 * Attempt to validate the {@code phone} format attribute.
 *
 * <p>The draft says the phone MAY match E.123. Quite vague. Here we use
 * Google's <a href="http://code.google.com/p/libphonenumber/">libphonenumber
 * </a> as it is a library specialized in phone number recognition.</p>
 *
 * <p>It will only chek if this is a potential phone number, not whether it is
 * actually valid for your country! If you really want that, you will probably
 * want to write your own {@link FormatAttribute}.</p>
 */
//TODO: more tests?
public final class PhoneAttribute
    extends AbstractFormatAttribute
{
    private static final PhoneNumberUtil PARSER = PhoneNumberUtil.getInstance();

    private static final FormatAttribute INSTANCE = new PhoneAttribute();

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    private PhoneAttribute()
    {
        super("phone", NodeType.STRING);
    }

    @Override
    public void validate(final ProcessingReport report, final FullData data)
        throws ProcessingException
    {
        final String input = data.getInstance().getNode().textValue();
        /*
         * The libphonenumber API doc says that no matter what region you put
         * when validating national phone numbers, the number is not actually
         * considered valid for a specific country without further
         * verifications. International phone numbers MUST start with a
         * "+" however, this is a constant.
         *
         * So, this is the only switching point: if it starts with a "+",
         * check with the "no zone" specification, otherwise check with any
         * country code.
         */
        try {
            if (input.startsWith("+"))
                PARSER.parse(input, "ZZ");
            else
                PARSER.parse(input, "FR");
        } catch (NumberParseException ignored) {
            report.error(newMsg(data, INVALID_PHONE_NUMBER));
        }
    }
}
