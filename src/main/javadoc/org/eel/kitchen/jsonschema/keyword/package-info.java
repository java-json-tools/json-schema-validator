/**
 * Abstract classes to implement keyword validators
 *
 * <p>Keyword validators are the meat of the validation process and validate
 * JSON instances. They are registered to a
 * {@link org.eel.kitchen.jsonschema.factories.KeywordFactory} with the list
 * of types they can validate. If an instance is of a type the keyword cannot
 * validate, the corresponding validator will not be used for this validation
 * step.
 * </p>
 *
 * <p>In the same vein as schema validation, instance validation is not done
 * in depth, but only at the current level. Some validators, though,
 * may need to spawn other validators (for instance, {@code dependencies} if
 * a schema dependency is encountered).
 * </p>
 *
 * <p>Most of the time, you will subclass
 * {@link org.eel.kitchen.jsonschema.keyword.KeywordValidator}, or
 * {@link org.eel.kitchen.jsonschema.keyword.NumericInstanceKeywordValidator}
 * if you register a new keyword for numeric instances (integers,
 * numbers). The last class is very specialized and handles the {@code type}
 * and {@code disallow} keywords, you will normally never have to use it.
 * </p>
 */
package org.eel.kitchen.jsonschema.keyword;