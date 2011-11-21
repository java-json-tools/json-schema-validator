/**
 * The main interface
 *
 * <p>This package contains all classes which are at the core of the validation
 * process, and first of all {@link
 * org.eel.kitchen.jsonschema.main.JsonValidator}. This is the class you will
 * instantiate for all validations you want to perform.</p>
 *
 * <p>The {@link org.eel.kitchen.jsonschema.main.ValidationContext} is a crucial
 * class which is passed as an argument to all validators, and which has roles
 * as diverse as generating new validators, providing {@link
 * org.eel.kitchen.jsonschema.main.ValidationReport} instances to them, giving
 * them access to the schema (via its {@link
 * org.eel.kitchen.jsonschema.main.SchemaProvider}, etc.</p>
 *
 * <p>A sample validation process will look like this:</p>
 *
 * <pre>
 * final JsonNode schema = JsonLoader.fromResource("/path/to/schema.json");
 * final JsonNode instance = JsonLoader.fromReader(someReader);
 *
 * final JsonValidator validator = new JsonValidator(schema);
 *
 * final ValidationReport report = validator.validate(instance);
 *
 * // Treat report
 * </pre>
 *
 * <p>Of course, much more can be done. See the different classes for
 * details.</p>
 */
package org.eel.kitchen.jsonschema.main;
