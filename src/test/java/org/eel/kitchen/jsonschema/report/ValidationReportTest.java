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

package org.eel.kitchen.jsonschema.report;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public final class ValidationReportTest
{
    @Test
    public void oneFatalMessageClearsAllOthers()
    {
        final Message.Builder msg = Domain.VALIDATION.newMessage()
            .setKeyword("N/A").setMessage("foo");

        final ValidationReport report = new ValidationReport();

        report.addMessage(msg.build());
        assertFalse(report.hasFatalError());

        report.addMessage(msg.build());
        assertFalse(report.hasFatalError());
        assertEquals(report.asJsonArray().size(), 2);

        report.addMessage(msg.setFatal(true).build());
        assertTrue(report.hasFatalError());
        assertEquals(report.asJsonArray().size(), 1);
    }

    @Test
    public void oneFatalMessageInAListClearsAllOthers()
    {
        final Message.Builder msg = Domain.VALIDATION.newMessage()
            .setKeyword("N/A").setMessage("foo");

        final ValidationReport report = new ValidationReport();

        final List<Message> list = Lists.newArrayList();

        list.add(msg.build());
        list.add(msg.setFatal(true).build());
        list.add(msg.setFatal(false).build());

        report.addMessages(list);

        assertTrue(report.hasFatalError());
        assertEquals(report.asJsonArray().size(), 1);
    }

    @Test
    public void mergingWithAnotherReportKeepsFatalStatus()
    {
        final Message.Builder msg = Domain.VALIDATION.newMessage()
            .setKeyword("N/A").setMessage("foo");

        ValidationReport r1, r2;

        // r1 is fatal
        r1 = new ValidationReport();
        r2 = new ValidationReport();

        r1.addMessage(msg.setFatal(true).build());
        r2.addMessage(msg.setFatal(false).build());

        r1.mergeWith(r2);
        assertTrue(r1.hasFatalError());
        assertEquals(r1.asJsonArray().size(), 1);

        // r2 is fatal
        r1 = new ValidationReport();
        r2 = new ValidationReport();

        r1.addMessage(msg.setFatal(false).build());
        r2.addMessage(msg.setFatal(true).build());

        r1.mergeWith(r2);
        assertTrue(r1.hasFatalError());
        assertEquals(r1.asJsonArray().size(), 1);
    }
}
