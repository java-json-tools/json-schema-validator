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

package com.github.fge.jsonschema.processing.syntax;

import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public final class ValidatedPaths
{
    private final ReentrantLock lock = new ReentrantLock();
    private final Set<JsonPointer> validatedPaths = Sets.newHashSet();
    private final Set<JsonPointer> uncheckedPaths = Sets.newHashSet();
    private final List<ProcessingMessage> messages = Lists.newArrayList();

    private boolean valid = false;

    public ReentrantLock getLock()
    {
        return lock;
    }

    public boolean isValidated(final JsonPointer currentPointer)
    {
        final JsonPointer closestUnchecked
            = closestPointer(uncheckedPaths, currentPointer);
        final JsonPointer closestValidated
            = closestPointer(validatedPaths, currentPointer);

        if (closestValidated == null)
            return false;
        if (closestUnchecked == null)
            return true;
        return closestUnchecked.isParentOf(closestValidated);
    }

    public void addValidatedPath(final JsonPointer pointer)
    {
        validatedPaths.add(pointer);
        uncheckedPaths.remove(pointer);
    }

    public void addUncheckedPath(final JsonPointer pointer)
    {
        uncheckedPaths.add(pointer);
    }

    public void addReport(final ProcessingReport report)
    {
        messages.addAll(report.getMessages());
        valid = report.isSuccess();
    }

    public boolean isValid()
    {
        return valid;
    }

    private static JsonPointer closestPointer(final Set<JsonPointer> set,
        final JsonPointer pointer)
    {
        JsonPointer ret = null;
        for (final JsonPointer ptr: set) {
            if (!ptr.isParentOf(pointer))
                continue;
            if (ret == null)
                ret = ptr;
            else if (ret.isParentOf(ptr))
                ret = ptr;
        }

        return ret;
    }
}
