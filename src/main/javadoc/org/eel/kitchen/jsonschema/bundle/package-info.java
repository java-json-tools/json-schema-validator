/**
 * Provides a bundle structure for {@link
 * org.eel.kitchen.jsonschema.syntax.SyntaxValidator} and {@link
 * org.eel.kitchen.jsonschema.keyword.KeywordValidator} instances
 *
 * <p>The two concrete implementations, {@link
 * org.eel.kitchen.jsonschema.bundle.DraftV3ValidatorBundle} and {@link
 * org.eel.kitchen.jsonschema.bundle.DraftV4ValidatorBundle}, are used by {@link
 * org.eel.kitchen.util.SchemaVersion}. On initialization, a {@link
 * org.eel.kitchen.jsonschema.factories.ValidatorFactory} will grab one and pass
 * them as constructor arguments to its {@link
 * org.eel.kitchen.jsonschema.factories.SyntaxFactory} and {@link
 * org.eel.kitchen.jsonschema.factories.KeywordFactory}.</p>
 *
 * <p>Right now, the API does not yet allow to register your own bundle to a
 * {@link org.eel.kitchen.jsonschema.main.JsonValidator}. However, you can use
 * the latter to register and unregister keywords -- or add new ones.</p>
 */
package org.eel.kitchen.jsonschema.bundle;
