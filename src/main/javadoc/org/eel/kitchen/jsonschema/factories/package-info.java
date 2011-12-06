/**
 * Validator factories and caching
 *
 * <p>Validator factories are here to provide validators for three different
 * purposes:
 * </p>
 * <ul>
 *     <li>schema syntax checking,</li>
 *     <li>JSON instance validation,</li>
 *     <li>and the particular case of {@code format}.</li>
 * </ul>
 *
 * <p>They also provide interfaces to register and unregister new validators
 * (except for {@code format} -- this may change in the furture) via the main
 * factory, which is {@link
 * org.eel.kitchen.jsonschema.factories.ValidatorFactory}. The  latter is also
 * in charge of caching:
 * </p>
 * <ul>
 *     <li>if a schema has already been validated, it will not be validated
 *     again;
 *     </li>
 *     <li>if an instance validator already exists for a given schema and JSON
 *     instance type, it will be retrieved from the cache and not be
 *     instantiated again.</li>
 * </ul>
 */
package org.eel.kitchen.jsonschema.factories;