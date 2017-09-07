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

package com.github.fge.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.ProcessingResult;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import java.util.Iterator;
import java.util.List;

/**
 * Single-schema instance validator
 *
 * <p>This is the interface you will use the most often. It is, in essence, a {@link
 * JsonValidator} initialized with a single JSON Schema. Note however that this
 * class still retains the ability to resolve JSON References.</p>
 */
public interface JsonSchema {
    /**
     * Validate an instance and return a processing report
     *
     * @param instance the instance to validate
     * @param deepCheck validate children even if container (array, object) is
     * invalid
     * @return a processing report
     * @throws ProcessingException a processing error occurred during validation
     *
     * @see JsonValidator#validate(JsonNode, JsonNode, boolean)
     *
     * @since 2.1.8
     */
    ProcessingReport validate(JsonNode instance, boolean deepCheck)
            throws ProcessingException;

    /**
     * Validate an instance and return a processing report
     *
     * <p>This calls {@link #validate(JsonNode, boolean)} with {@code false} as
     * a second argument.</p>
     *
     * @param instance the instance to validate
     * @return a processing report
     * @throws ProcessingException a processing error occurred during validation
     */
    ProcessingReport validate(JsonNode instance)
            throws ProcessingException;

    /**
     * Validate an instance and return a processing report (unchecked version)
     *
     * <p>Unchecked validation means that conditions which would normally cause
     * the processing to stop with an exception are instead inserted into the
     * resulting report.</p>
     *
     * <p><b>Warning</b>: this means that anomalous events like an unresolvable
     * JSON Reference, or an invalid schema, are <b>masked</b>!</p>
     *
     * @param instance the instance to validate
     * @param deepCheck validate children even if container (array, object) is
     * invalid
     * @return a report (a {@link ListProcessingReport} if an exception was
     * thrown during processing)
     *
     *
     * @see ProcessingResult#uncheckedResult(Processor, ProcessingReport,
     * MessageProvider)
     * @see JsonValidator#validate(JsonNode, JsonNode, boolean)
     *
     * @since 2.1.8
     */
    ProcessingReport validateUnchecked(JsonNode instance,
                                              boolean deepCheck);
    /**
     * Validate an instance and return a processing report (unchecked version)
     *
     * <p>This calls {@link #validateUnchecked(JsonNode, boolean)} with {@code
     * false} as a third argument.</p>
     *
     * @param instance the instance to validate
     * @return a report (a {@link ListProcessingReport} if an exception was
     * thrown during processing)
     */
    ProcessingReport validateUnchecked(JsonNode instance);

    /**
     * Check whether an instance is valid against this schema
     *
     * @param instance the instance
     * @return true if the instance is valid
     * @throws ProcessingException an error occurred during processing
     */
    boolean validInstance(JsonNode instance)
            throws ProcessingException;

    /**
     * Check whether an instance is valid against this schema (unchecked
     * version)
     *
     * <p>The same warnings apply as described in {@link
     * #validateUnchecked(JsonNode)}.</p>
     *
     * @param instance the instance to validate
     * @return true if the instance is valid
     */
    boolean validInstanceUnchecked(JsonNode instance);

    /**
     * Method to retrieve all JSON Schema property names.
     *
     * @return An iterator with all property names
     */
    Iterator<String> getPropertyNames();

    /**
     * Method to retrieve a JSON Schema attribute enum values.
     * If no matching attribute is found, returns null.
     *
     * @param name Name of attribute to look for
     *
     * @return List of the enum values of the attribute, if is enum type; empty if it is not
     */

    List<String> getPropertyEnum(final String name);

    /**
     * Method to retrieve a JSON Schema property type.
     * If no matching attribute is found, returns null.
     *
     * @param name Name of property to look for
     *
     * @return a JSON Schema property type as text
     */

    String getPropertyType(final String name);

    /**
     * Method to retrieve a JSON Schema property description.
     * If no matching attribute is found, returns null.
     *
     * @param name Name of property to look for
     *
     * @return a JSON Schema property description as text
     */

    String getPropertyDescription(final String name);

    /**
     * Method for checking if a JSON Schema attribute with specified name is required.
     * If no matching attribute is found, returns null.
     *
     * @param name Name of attribute to look for
     *
     * @return true if it is required, false if not
     */
    boolean isRequired(final String name);
}
