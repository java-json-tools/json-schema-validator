<p>This is an implementation of the [JSON Schema
specification](http://www.json-schema.org) written in pure Java. This allows
pure server side JSON schema validation if this is what you are looking for.

<p>The draft serving as a reference is, at the moment, draft version 3, which
can be found [here](http://json-schema.org/draft-03/schema) (version 4 is
around the corner, for some defintion of "corner" -- understand, days, or a few
weeks). This implementation has <b>full</b> support for instance validation
using draft v3, and can be extended to support draft v4 easily.

<p><b>Version 0.4 is out</b>: see [here]
(https://github.com/fge/json-schema-validator/wiki/ChangeLog) for a list of
features. It now has experimental draft v4 support.</p>

<p>Small overview of available features:

* full draft v3 validation support, and experimental draft v4 validation
  support;
* full report or fail-fast report modes (ie, go deep or fail at first error);
* arbitrary length/precision number validation;
* validator caching for performance;
* ECMA 262 regexes (using Rhino), as required by the draft;
* schema syntax validation, and the possibility to skip it;
* ability to register URI handlers for any scheme (HTTP only natively);
* complete <tt>$ref</tt> support, with loop detection;
* ability to determine the default schema version to use (draft v3 by default);
* ability to register/unregister keywords against a specific schema version;
* automatic schema version switching if <tt>$schema</tt> is encountered within a
  schema.

<p>I believe this to be the most complete implementation of JSON Schema in Java
today.

<p>For a detailed discussion of the implementation, see
[here](https://github.com/fge/json-schema-validator/wiki/Status). For a list of
planned features for next versions, see
[here](https://github.com/fge/json-schema-validator/wiki/Roadmap).

Please see the [wiki](https://github.com/fge/json-schema-validator/wiki/) for
more details.

