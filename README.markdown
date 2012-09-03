<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file
src/main/resources/LICENSE for the full text.</p>

<p>The current stable version is <b>1.0</b>. The older, still supported version
is <b>0.6.1</b>. See the
<a
href="https://github.com/fge/json-schema-validator/wiki/ChangeLog">ChangeLog</a>
for more details.</p>

<p>At any point in time, the javadoc for the current stable version can be found
<a href="http://fge.github.com/json-schema-validator/">here</a>. The javadoc
contains code samples.</p>

<p><b>IMPORTANT 1:</b> the versioning scheme is changing, see below for more
information.</p>

<p><b>IMPORTANT 2:</b> starting with 1.0, some format specifiers are split from
this package and are in a new one:
<a href="https://github.com/fge/json-schema-formats">json-schema-formats</a>.
Support for the following format specifiers are not in the stable version
anymore:</p>

* <tt>date</tt>,
* <tt>time</tt>,
* <tt>phone</tt>,
* <tt>utc-millisec</tt>,
* <tt>date-time-ms</tt>.

<p>Support for other format specifiers are still in this package for various
reasons, see the above project for further explanations.</p>

<h2>Versioning scheme policy</h2>

<p>The versioning scheme is now defined by the middle number of the version
number:</p>

* if this number is <b>even</b>, then this is the <b>stable</b> version; no new
  features will be added to such versions, and the user API will only see
  incremental changes;
* if this number is <b>odd</b>, then this is the <b>development</b> version; new
  features will be added to those versions only, and the user API may change.

<p>The next development version will therefore be <b>1.1</b>.

<h2>What it is</h2>

<p>This is an implementation of the <a href="http://json-schema.github.com">JSON
Schema specification</a> written in pure Java.  This allows pure server side
JSON schema validation if this is what you are looking for.<p>

<p>This implementation has the following features:</p>

* thread safe, concurrent-friendly validation;
* <a href="https://github.com/fge/json-schema-validator/wiki/Performance">very
  fast</a>;
* full draft v3 validation (however, see above for <tt>format</tt>);
* full <tt>$ref</tt> support, including <tt>id</tt> resolving and loop
  detection;
* validation of numeric JSON instances of arbitrary scale/precision;
* ECMA 262 regexes (using Rhino);
* ability to register/unregister URI handlers for arbitrary URI schemes;
* ability to set a default URI namespace;
* ability to define URI redirections;
* ability to register/unregister keywords and format specifiers.

<h2>Relevant documents</h2>

<p>Currently, JSON Schema is not an official RFC. In fact, the existing draft is
obsolete -- but it is used on the Internet nonetheless. This implementation is
based on the following drafts:</p>

* <a href="http://tools.ietf.org/html/draft-zyp-json-schema-03">JSON Schema
  Internet draft, version 3</a>;
* <a href="http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-02">JSON
  Reference Internet draft, version 2</a>;
* <a href="http://tools.ietf.org/html/draft-ietf-appsawg-json-pointer-03">JSON
  Pointer Internet draft, version 3</a>.

<h2>More...</h2>

<p>For a detailed discussion of the implementation, see <a
href="https://github.com/fge/json-schema-validator/wiki/Status">here</a>. For a
list of planned features for next versions, see <a
href="https://github.com/fge/json-schema-validator/wiki/Roadmap">here</a>.

Please see the <a
href="https://github.com/fge/json-schema-validator/wiki/">wiki</a> for more
details.

