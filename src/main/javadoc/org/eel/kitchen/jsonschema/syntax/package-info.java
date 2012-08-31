/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Schema syntax validation package
 *
 * <p>The main class in this package is {@link
 * org.eel.kitchen.jsonschema.syntax.SyntaxValidator}.</p>
 *
 * <p>Syntax validation has a critically important role in the validation
 * process. An invalid schema will always <i>fail</i> to invalidate a JSON
 * instance.</p>
 *
 * <p>For this implementation in particular, it also helps to ensure that the
 * {@link org.eel.kitchen.jsonschema.keyword.KeywordValidator} associated with
 * the schema keyword does not need to preoccupy about its arguments being
 * well-formed -- they will be since the syntax validator has verified that they
 * are.</p>
 *
 * <p>Unlike keyword validators, syntax validators are not built by reflection.
 * It is therefore your responsibility to instantiate it and only then register
 * it.</p>
 *
 * <p>Here is an example code for a hypothetic {@code foo} keyword which must
 * have a string as an argument, and this string must be at least 5 characters
 * long:</p>
 *
 * <pre>
 *  public final class SyntaxCheckerImpl
 *      implements SyntaxChecker
 *  {
 *      &#64;Override
 *      public void checkSyntax(final ValidationMessage.Builder msg,
 *          final List&lt;String&gt; messages, final JsonNode schema)
 *      {
 *          final JsonNode node = schema.get(keyword);
 *          if (!node.isTextual()) {
 *              msg.setMessage("field is not a string");
 *              messages.add(msg.build());
 *              return;
 *          }
 *
 *          if (node.textValue().length() >= 5)
 *              return;
 *
 *          msg.setMessage("field has insufficient length")
 *              .addInfo("required", 5)
 *              .addInfo("found", node.textValue().length());
 *          messages.add(msg.build());
 *      }
 *  }
 * </pre>
 *
 * <p>For more information, see {@link
 * org.eel.kitchen.jsonschema.syntax.SyntaxChecker} and {@link
 * org.eel.kitchen.jsonschema.report.ValidationMessage}.</p>
 */
package org.eel.kitchen.jsonschema.syntax;
