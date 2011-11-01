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

package eel.kitchen.jsonschema.container;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.keyword.KeywordValidatorFactory;
import eel.kitchen.jsonschema.base.CombinedValidator;
import eel.kitchen.jsonschema.base.Validator;
import org.codehaus.jackson.JsonNode;

public abstract class ContainerValidator
    extends CombinedValidator
{
    protected final Validator validator;

    protected ContainerValidator(final Validator validator,
        final KeywordValidatorFactory factory, final JsonNode schema,
        final JsonNode instance)
    {
        super(factory, schema, instance);
        this.validator = validator;
    }

    protected abstract void buildQueue();

    @Override
    public final ValidationReport validate()
    {
        report.mergeWith(validator.validate());

        if (!report.isSuccess())
            return report;

        buildQueue();

        while (hasMoreElements() && report.isSuccess())
            report.mergeWith(nextElement().validate());

        queue.clear();
        return report;
    }
}
