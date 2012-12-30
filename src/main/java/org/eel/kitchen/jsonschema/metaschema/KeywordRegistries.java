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

package org.eel.kitchen.jsonschema.metaschema;

/**
 * Builtin keyword and format attribute registries
 *
 * <p>Do not use this class directly: use {@link MetaSchema} instead.</p>
 */
public final class KeywordRegistries
{
    // No instantiation
    private KeywordRegistries()
    {
    }

    /**
     * Return a registry for draft version 3 core schema
     *
     * @return a {@link KeywordRegistry}
     */
    public static KeywordRegistry draftV3Core()
    {
        final KeywordRegistry ret = new KeywordRegistry();
        ret.addSyntaxCheckers(SyntaxCheckers.draftV3Core());
        ret.addValidators(KeywordValidators.draftV3());
        ret.addFormatAttributes(FormatAttributes.draftV3());
        return ret;
    }

    /**
     * Return a registry for draft version 3 hyper schema
     *
     * @return a {@link KeywordRegistry}
     */
    public static KeywordRegistry draftV3HyperSchema()
    {
        final KeywordRegistry ret = new KeywordRegistry();
        ret.addSyntaxCheckers(SyntaxCheckers.draftV3HyperSchema());
        ret.addValidators(KeywordValidators.draftV3());
        ret.addFormatAttributes(FormatAttributes.draftV3());
        return ret;
    }

    /**
     * Return a registry for draft version 4
     *
     * @return a {@link KeywordRegistry}
     */
    public static KeywordRegistry draftV4Core()
    {
        final KeywordRegistry ret = new KeywordRegistry();
        ret.addSyntaxCheckers(SyntaxCheckers.draftV4Core());
        ret.addValidators(KeywordValidators.draftV4());
        ret.addFormatAttributes(FormatAttributes.draftV4());
        return ret;
    }

    /**
     * Return the current default keyword registry (draft v3)
     *
     * @return a {@link KeywordRegistry}
     */
    public static KeywordRegistry defaultRegistry()
    {
        return draftV3Core();
    }
}
