/**
 * Various utility packages
 *
 * <p>The one you will use the most often here is {@link
 * org.eel.kitchen.jsonschema.util.JsonLoader}: it contains various methods to
 * load JSON documents as {@link com.fasterxml.jackson.databind.JsonNode}
 * instances.
 * </p>
 *
 * <p>You may want to have a look at {@link
 * org.eel.kitchen.jsonschema.util.RhinoHelper}, which is in charge of all regex
 * validation: as the standard dictates ECMA 262 regexes, using {@link
 * java.util.regex} is out of the question. See this class' description for more
 * details.
 * </p>
 *
 * <p>The {@link org.eel.kitchen.jsonschema.util.NodeType} enum is a critical
 * part of the code. Its ability to determine the type of a {@link JsonNode} is
 * an essential part of the validation process.</p>
 *
 * <p>Finally, the {@link org.eel.kitchen.jsonschema.util.JacksonUtils} class
 * provides useful methods to perform common operations on exsting {@link
 * com.fasterxml.jackson.databind.JsonNode} instances,
 * not provided by Jackson per se.
 * </p>
 */
package org.eel.kitchen.jsonschema.util;
