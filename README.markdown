<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file
src/main/resources/LICENSE for the full text.</p>

<p>Versions:</p>

<ul>
    <li>current development version: <b>1.1.3</b> (<a
href="https://github.com/fge/json-schema-validator/wiki/ChangeLog.devel">ChangeLog</a>,
<a href="http://fge.github.com/json-schema-validator/devel/index.html">Javadoc</a>);</li>
    <li>current stable version: <b>1.0.4</b> (<a
href="https://github.com/fge/json-schema-validator/wiki/ChangeLog.stable">ChangeLog</a>,
<a href="http://fge.github.com/json-schema-validator/stable/index.html">Javadoc</a>).</li>
</ul>

<p><b>Note:</b> this implementation does not cover the full set of format
attributes defined by the currently active draft, and this is on purpose. See <a
href="https://github.com/fge/json-schema-formats">json-schema-formats</a> (FWIW,
at the Maven level, <tt>json-schema-formats</tt> depends on
<tt>json-schema-validator</tt>).</p>

<p>Note: the javadoc contains code samples.</p>

<h2>Versioning scheme policy</h2>

<p>The versioning scheme is defined by the middle number of the version
number:</p>

* if this number is <b>even</b>, then this is the <b>stable</b> version; no new
  features will be added to such versions, and the user API will only see
  incremental changes, never modifications;
* if this number is <b>odd</b>, then this is the <b>development</b> version; new
  features will be added to those versions only, and the user API may change.

<h2>What it is</h2>

<p>This is an implementation of the <a
href="https://github.com/json-schema/json-schema">JSON Schema specification</a>
written in pure Java.  This allows pure server side JSON schema validation if
this is what you are looking for.<p>

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
* ability to register/unregister keywords and format attributes.

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

