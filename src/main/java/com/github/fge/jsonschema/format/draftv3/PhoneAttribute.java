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

package com.github.fge.jsonschema.format.draftv3;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

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
    public void validate(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
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
            report.error(newMsg(data, bundle, "err.format.invalidPhoneNumber")
                .putArgument("value", input));
        }
    }
}
