/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * <p>Base date validation format. All implementations of date format validation
 * (save for {@link UnixEpochFormatValidator}) use date formats which can be
 * reproduced using Java's {@link SimpleDateFormat}. The validation process
 * is therefore always the same, and implemented here.</p>
 *
 * <p>In the same vein as {@link AbstractFormatValidator},
 * <code>doSetup()</code> is also overriden here to always return true, as
 * it is up to {@link FormatValidator} to do the real schema validation.</p>
 */
public abstract class AbstractDateFormatValidator
    extends AbstractValidator
{
    /**
     * The {@link SimpleDateFormat} instance
     */
    private final SimpleDateFormat format;

    /**
     * The error message in the event of an instance validation failure
     */
    private final String errmsg;

    /**
     * Sole constructor. It will build a {@link SimpleDateFormat} instance
     * out of the format parameter, and the error message out of the
     * description parameter. Note that the format parameter MUST be valid.
     *
     * @param fmt the date format
     * @param desc the format description
     * @throws IllegalArgumentException if the format is not valid (but see
     * above)
     * @throws NullPointerException if the format is null (but again,
     * see above)
     */
    protected AbstractDateFormatValidator(final String fmt, final String desc)
    {
        format = new SimpleDateFormat(fmt);
        errmsg = String.format("value is not a valid %s", desc);
    }

    @Override
    protected boolean doSetup()
    {
        return true;
    }

    /**
     * Validate an instance. It is just as simple as calling
     * <code>parse()</code> on the {@link SimpleDateFormat} instance for this
     * validator.
     *
     * @param node the instance to validate
     * @return true if the instance is valid
     */
    @Override
    protected final boolean doValidate(final JsonNode node)
    {
        try {
            format.parse(node.getTextValue());
            return true;
        } catch (ParseException e) {
            messages.add(errmsg);
            return false;
        }
    }
}
