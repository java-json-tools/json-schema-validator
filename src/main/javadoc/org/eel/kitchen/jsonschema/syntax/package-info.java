/**
 * Abstract classes to implement syntax validators
 *
 * <p>Syntax validators are used to validate your schemas. Their role is
 * fundamental, since they guarantee that {@link
 * org.eel.kitchen.jsonschema.keyword.KeywordValidator} instances need not worry
 * about malformed schemas.
 * </p>
 *
 * <p>Note that schemas are not checked in depth, but only at the level where
 * they are currently enforced. For schemas validating object nodes, for
 * instance, if the structure of the object fails to validate against the
 * schema, subschemas validating children will not be used, and therefore will
 * not be checked either.
 * </p>
 *
 * <p>You will probably never need to subclass
 * {@link org.eel.kitchen.jsonschema.syntax.SyntaxValidator} directly: other
 * abstract classes in this package handle some of the work for you in 90+% of
 * cases, so you will probably use one of those instead (especially {@link
 * org.eel.kitchen.jsonschema.syntax.SimpleSyntaxValidator}).
 * </p>
 */
package org.eel.kitchen.jsonschema.syntax;
