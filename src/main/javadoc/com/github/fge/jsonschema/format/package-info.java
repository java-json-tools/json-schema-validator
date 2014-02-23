/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

/**
 * Format attribute base classes
 *
 * <p>The {@code format} keyword plays a particular role in JSON Schema
 * validation, since it is the only keyword performing semantic validation.
 * Note that supporting it is <i>optional</i>, and that implementations are
 * asked to provide a way to deactivate format checking on demand. This
 * implementation offers this option.</p>
 *
 * <p>Draft v4 defines a narrower subset of format attributes than draft v3.
 * The following format attributes are common to both draft v3 and draft v4:</p>
 *
 * <ul>
 *     <li>{@code date-time};</li>
 *     <li>{@code email};</li>
 *     <li>{@code host-name};</li>
 *     <li>{@code ipv6};</li>
 *     <li>{@code regex};</li>
 *     <li>{@code uri}.</li>
 * </ul>
 *
 * <p>Draft v3 defines the following additional attributes:</p>
 *
 * <ul>
 *     <li>{@code date};</li>
 *     <li>{@code phone};</li>
 *     <li>{@code time};</li>
 *     <li>{@code utc-millisec};</li>
 *     <li>{@code color} (<b>unsupported</b>);</li>
 *     <li>{@code style} (<b>unsupported</b>).</li>
 * </ul>
 *
 * <p>Additionally, there is an attribute named {@code ip-address} in draft v3,
 * and {@code ipv4} in draft v4, which can validate IPv4 addresses.</p>
 */

package com.github.fge.jsonschema.format;