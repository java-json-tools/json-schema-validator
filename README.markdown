<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file
src/main/resources/LICENSE for the full text.</p>

<p><b>IMPORTANT: if you report a bug, please mention what version you are
using! Please note that versions lower than 0.4.x are not supported
anymore.</b></p>

<p>The current version is <b>0.5.0beta5</b>. The major feature of this version
is a stabilized user API. See the
[ChangeLog](https://github.com/fge/json-schema-validator/wiki/ChangeLog) for
more details. The old, still maintained version is <b>0.4.1</b>. 

<h2>What it is</h2>

<p>This is an implementation of the
[JSON Schema specification](http://www.json-schema.org) written in pure Java.
This allows pure server side JSON schema validation if this is what you are
looking for./<p>

<p>The draft serving as a reference is, at the moment, draft version 3.</p>

<p>The current version (0.5.x) has the following features:</p>

* full draft v3 validation (except for the <tt>color</tt> and <tt>style</tt>
  format specifiers...);
* arbitrary length/precision number validation;
* ECMA 262 regexes (using Rhino);
* schema caching for performance;
* schema syntax validation;
* full <tt>$ref</tt> support, including <tt>id</tt> resolving <i>and loop
  detection</i>;
* <b>thread safe validators</b> (0.4.x validation is not thread safe);
* ability to register URI handlers for any scheme (HTTP only natively);
* ability to register/unregister keywords.

<h2>More...</h2>

<p>For a detailed discussion of the implementation, see
[here](https://github.com/fge/json-schema-validator/wiki/Status). For a list of
planned features for next versions, see
[here](https://github.com/fge/json-schema-validator/wiki/Roadmap).

Please see the [wiki](https://github.com/fge/json-schema-validator/wiki/) for
more details.

