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

package eel.kitchen.jsonschema.keyword;

import eel.kitchen.jsonschema.base.SimpleValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.keyword.format.FormatFactory;
import org.codehaus.jackson.JsonNode;

public final class FormatValidator
    extends SimpleValidator
{
    private final FormatFactory formatFactory = new FormatFactory();

    public FormatValidator(final KeywordValidatorFactory ignored,
        final JsonNode schema, final JsonNode instance)
    {
        super(ignored, schema, instance);
    }

    @Override
    protected void validateInstance()
    {
        final String fmt = schema.get("format").getTextValue();

        final Validator validator
            = formatFactory.getFormatValidator(fmt, instance);

        report.mergeWith(validator.validate());
    }

}
