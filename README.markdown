<h2>Read me first</h2>

<p><b>IMPORTANT: if you report a bug, please mention what version you are
using!</b></p>

<p>Current stable version is <b>0.4.1</b>. See the
[ChangeLog](https://github.com/fge/json-schema-validator/wiki/ChangeLog) for
more details.</p>

<h2>What it is</h2>

<p>This is an implementation of the [JSON Schema
specification](http://www.json-schema.org) written in pure Java. This allows
pure server side JSON schema validation if this is what you are looking for./<p>

<p>The draft serving as a reference is, at the moment, draft version 3, which
can be found [here](http://json-schema.org/draft-03/schema).</p>

<p>The current stable version (0.4.x) is pretty much feature complete:</p>

* full draft v3 validation support, and experimental draft v4 validation
  support;
* full report or fail-fast report modes (ie, go deep or fail at first error);
* arbitrary length/precision number validation;
* validator and schema caching for performance;
* ECMA 262 regexes (using Rhino), as required by the draft;
* schema syntax validation, and the possibility to skip it;
* ability to register URI handlers for any scheme (HTTP only natively);
* <tt>$ref</tt> support, with loop detection;
* ability to determine the default schema version to use (draft v3 by default);
* ability to register/unregister keywords against a specific schema version;
* automatic schema version switching if <tt>$schema</tt> is encountered within a
  schema.

<p>However, the project is currently undergoing a rewrite, which means some
features will disappear before returning for 0.5.</p>

<p>For a detailed discussion of the implementation, see
[here](https://github.com/fge/json-schema-validator/wiki/Status). For a list of
planned features for next versions, see
[here](https://github.com/fge/json-schema-validator/wiki/Roadmap).

Please see the [wiki](https://github.com/fge/json-schema-validator/wiki/) for
more details.

