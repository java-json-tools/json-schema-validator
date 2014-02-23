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
 * Main interface to validation
 *
 * <p>This package contains wrapper classes over validation processors.</p>
 *
 * <p>The main provider is {@link
 * com.github.fge.jsonschema.main.JsonSchemaFactory}: from this class, you can
 * obtain a {@link com.github.fge.jsonschema.processors.syntax.SyntaxValidator},
 * a {@link com.github.fge.jsonschema.main.JsonValidator} or a {@link
 * com.github.fge.jsonschema.main.JsonSchema}.</p>
 */
package com.github.fge.jsonschema.main;
