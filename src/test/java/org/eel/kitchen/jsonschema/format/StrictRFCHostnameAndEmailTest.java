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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.main.ValidationFeature;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.validator.ValidationContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class StrictRFCHostnameAndEmailTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private static final EnumSet<ValidationFeature> UNSTRICT
        = EnumSet.noneOf(ValidationFeature.class);

    private static final EnumSet<ValidationFeature> STRICT
        = EnumSet.of(ValidationFeature.STRICT_RFC_CONFORMANCE);

    private FormatSpecifier specifier;
    private ValidationContext context;
    private ValidationReport report;
    private JsonNode value;

    @DataProvider
    public Iterator<Object[]> getHostnameData()
    {
        final Set<Object[]> set = Sets.newHashSet();

        set.add(new Object[] { "foo", false, true });
        set.add(new Object[] { "foo.bar", true, true });

        return set.iterator();
    }

    @Test(dataProvider = "getHostnameData")
    public void hostnameStrictRFCConformanceIsObeyed(final String input,
        final boolean unstrictRet, final boolean strictRet)
    {
        specifier = HostnameFormatSpecifier.getInstance();
        value = factory.textNode(input);

        context = new ValidationContext(null, UNSTRICT);
        report = new ValidationReport();
        specifier.checkValue("", context, report, value);
        assertEquals(report.isSuccess(), unstrictRet);

        context = new ValidationContext(null, STRICT);
        report = new ValidationReport();
        specifier.checkValue("", context, report, value);
        assertEquals(report.isSuccess(), strictRet);
    }

    @DataProvider
    public Iterator<Object[]> getEmailData()
    {
        final Set<Object[]> set = Sets.newHashSet();

        set.add(new Object[] { "foo", false, true });
        set.add(new Object[] { "foo@bar", true, true });

        return set.iterator();
    }

    @Test(dataProvider = "getEmailData")
    public void emailStrictRFCConformanceIsObeyed(final String input,
        final boolean unstrictRet, final boolean strictRet)
    {
        specifier = EmailFormatSpecifier.getInstance();
        value = factory.textNode(input);

        context = new ValidationContext(null, UNSTRICT);
        report = new ValidationReport();
        specifier.checkValue("", context, report, value);
        assertEquals(report.isSuccess(), unstrictRet);

        context = new ValidationContext(null, STRICT);
        report = new ValidationReport();
        specifier.checkValue("", context, report, value);
        assertEquals(report.isSuccess(), strictRet);
    }
}
