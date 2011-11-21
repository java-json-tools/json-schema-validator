/**
 * Specialized validators for container nodes (arrays, objects)
 *
 * <p>These are the only validators which need (and do) validate in depth,
 * since by definition container nodes contain other nodes. They are passed
 * two arguments: the current schema, and a validator. The validator
 * validates the structure of the container, and only if this validator
 * succeeds, children are considered.</p>
 *
 * <p>Both classes have helper classes to help them grab the schema (or even
 * list of schemas, for object instances) used to validate one child. They
 * validate all children in order (naturally so for arrays,
 * or the sorted set of properties for objects). They will not stop at the
 * first failed validation and will continue until all children have been
 * validated. Of course, if the child is itself a container node,
 * the process goes on recursively.
 * </p>
 */
package org.eel.kitchen.jsonschema.container;