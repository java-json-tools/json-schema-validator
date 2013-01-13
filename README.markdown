<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.</p>

<p>This project has a dedicated <a
href="https://groups.google.com/forum/?fromgroups#!forum/json-schema-validator">Google
group</a>.</p>

<p>Versions:</p>

<ul>
    <li>current development version: <b>1.5.1</b> (<a
    href="https://github.com/fge/json-schema-validator/wiki/ChangeLog.devel">ChangeLog</a>, <a
    href="http://fge.github.com/json-schema-validator/devel/index.html">Javadoc</a>);</li>
    <li>current stable version: <b>1.4.4</b> (<a
    href="https://github.com/fge/json-schema-validator/wiki/ChangeLog.stable">ChangeLog</a>, <a
    href="http://fge.github.com/json-schema-validator/stable/index.html">Javadoc</a>);</li>
    <li>old stable version: <b>1.2.2</b> (<a
    href="http://fge.github.com/json-schema-validator/old/index.html">Javadoc</a>).</li>
</ul>

<p>If you are looking for a quick start, see the <a
href="http://fge.github.com/json-schema-validator/stable/index.html?org/eel/kitchen/jsonschema/examples/package-summary.html">code
examples</a>. You can see what is new in 1.4.x <a
href="https://github.com/fge/json-schema-validator/wiki/What's-new">here.</a></p>

<h2>Versioning scheme policy</h2>

<p>The versioning scheme is defined by the middle digit of the version number:</p>

* if this number is <b>even</b>, then this is the <b>stable</b> version; no new features will be
  added to such versions, and the user API will not change (save for some additions if requested).
* if this number is <b>odd</b>, then this is the <b>development</b> version; new features will be
  added to those versions only, and the user API may change.

<h2>What it is</h2>

<p>This is an implementation of all validation aspects (schema syntax validation; instance, aka JSON
data, validation) of the <a href="http://json-schema.org">JSON Schema specification</a> written in
pure Java. This allows pure server side JSON schema validation if this is what you are looking
for.<p>

<p>This implementation has the following features:</p>

* thread safe, concurrent-friendly validation;
* <a href="https://github.com/fge/json-schema-validator/wiki/Performance">very
  fast</a>;
* full draft v3 validation, including hyper schema;
* draft v4 validation;
* <tt>$schema</tt> detection;
* full schema addressing support;
* validation of numeric JSON instances of arbitrary scale/precision;
* ECMA 262 regexes (using Rhino);
* extensive customization: (un)registering URI schemes, setting namepaces, redirections, registering
  schemas/keywords, others.

<h2>Relevant documents</h2>

<p>Currently, JSON Schema is not an official RFC. In fact, the existing draft is obsolete -- but it
is used on the Internet nonetheless. This implementation is based on the following drafts:</p>

* draft v4 as currently defined by the <a href="https://github.com/json-schema/json-schema">JSON
  Schema working group</a>;
* <a href="http://tools.ietf.org/html/draft-zyp-json-schema-03">JSON Schema Internet draft, version
  3</a>;
* <a href="http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03">JSON Reference Internet draft,
  version 3</a>;
* <a href="http://tools.ietf.org/html/draft-ietf-appsawg-json-pointer-07">JSON Pointer Internet
  draft, version 7</a>.

<h2>More...</h2>

<p>For a detailed discussion of the implementation, see <a
href="https://github.com/fge/json-schema-validator/wiki/Status">here</a>. For a list of planned
features for next versions, see <a
href="https://github.com/fge/json-schema-validator/wiki/Roadmap">here</a>.

Please see the <a href="https://github.com/fge/json-schema-validator/wiki/">wiki</a> for more
details.

