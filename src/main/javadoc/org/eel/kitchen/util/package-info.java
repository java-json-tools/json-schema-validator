/**
 * Various utility packages
 *
 * <p>The one you will use the most often here is
 * {@link org.eel.kitchen.util.JsonLoader}: it contains various methods to
 * load JSON content (as {@link com.fasterxml.jackson.databind.JsonNode} instances),
 * which means schemas and instances to validate.</p>
 *
 * <p>You may want to have a look at {@link org.eel.kitchen.util.RhinoHelper},
 * which is in charge of all regex validation: as the standard dictates ECMA
 * 262 regexes, using {@link java.util.regex} is out of the question. See
 * this class' description for more details.
 * </p>
 *
 * <p>All classes in this package are important, in fact. But they are here
 * since they don't really fit anywhere else :)
 * </p>
 */
package org.eel.kitchen.util;