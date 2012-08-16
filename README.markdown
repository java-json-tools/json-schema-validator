<h2>Read me first</h2>

<p><b>IMPORTANT: if you report a bug, please mention what version you are
using!</b></p>

<p><b>IMPORTANT 2: the current development version, 0.5.x, makes no guarantees
as to the user API stability as long as it is labeled beta</b>. JSON Schema
usage is still a novelty, and it is quite a difficult task to foresee users'
needs. I make the API evolve according to my own feelings,
but those are _my_ feelings.</p>

<p>The current version is <b>0.5.0beta3</b>.</p>

<p>The old, still maintained version is <b>0.4.1</b>. See the
[ChangeLog](https://github.com/fge/json-schema-validator/wiki/ChangeLog) for
more details.</p>

<h2>What it is</h2>

<p>This is an implementation of the
[JSON Schema specification](http://www.json-schema.org) written in pure Java.
This allows pure server side JSON schema validation if this is what you are
looking for./<p>

<p>The draft serving as a reference is, at the moment, draft version 3.</p>

<p>The current version (0.5.x) has the following features:</p>

* full draft v3 validation (except for the <tt>color</tt> and <tt>style</tt>
  format specifiers, which nobody uses anyway, right?);
* arbitrary length/precision number validation;
* ECMA 262 regexes (using Rhino), as required by the draft;
* schema caching for performance;
* schema syntax validation (also cached);
* full <tt>$ref</tt> support, including <tt>id</tt> resolving <i>and loop
  detection</i>;
* <b>thread safe validators</b> (0.4.x validation is not thread safe).

<p>Features in 0.4.x which are not yet in 0.5.x (some of these are of
questionable use anyway):</p>

* experimental draft v4 validation;
* full report or fail-fast report modes (ie, go deep or fail at first error);
* ability to register URI handlers for any scheme (HTTP only natively);
* ability to determine the default schema version to use (draft v3 by default);
* ability to register/unregister keywords against a specific schema version;
* automatic schema version switching if <tt>$schema</tt> is encountered within a
  schema.

<p>Note that <tt>$ref</tt> support in 0.4.x is flaky.</p>

<p>For a detailed discussion of the implementation, see
[here](https://github.com/fge/json-schema-validator/wiki/Status). For a list of
planned features for next versions, see
[here](https://github.com/fge/json-schema-validator/wiki/Roadmap).

Please see the [wiki](https://github.com/fge/json-schema-validator/wiki/) for
more details.

