<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.</p>

<p>This is one of the very few JSON Schema implementations to have complete validation support for
the latest draft (v4).<p>

<p>Should you wonder about it, this library is reported to <a
href="http://stackoverflow.com/questions/14511468/java-android-validate-string-json-against-string-schema">work
on Android</a> :)</p>

<p>Quick start:</p>

<ul>
    <li>you can <a href="http://json-schema-validator.herokuapp.com">test this library online</a>
    (note: latest <b>development</b> version); you can even <a
    href="https://github.com/fge/json-schema-validator-demo">fork it and run it yourself</a>;</li>
    <li>you can have a look at the code samples: <a
    href="http://fge.github.com/json-schema-validator/stable/index.html?org/eel/kitchen/jsonschema/examples/package-summary.html">stable
    version</a>, <a
    href="http://fge.github.com/json-schema-validator/devel/index.html?com/github/fge/jsonschema/examples/package-summary.html">development
    version</a>.</li>
</ul>


<p>This project also has a dedicated <a
href="https://groups.google.com/forum/?fromgroups#!forum/json-schema-validator">Google
group</a>.</p>

<p>Versions:</p>

<ul>
    <li>development version: <b>1.99.2</b> (<a
    href="https://github.com/fge/json-schema-validator/wiki/ChangeLog.devel">ChangeLog</a>, <a
    href="http://fge.github.com/json-schema-validator/devel/index.html">Javadoc</a>);</li>
    <li>stable version: <b>1.4.9</b> (<a
    href="https://github.com/fge/json-schema-validator/wiki/ChangeLog.stable">ChangeLog</a>, <a
    href="http://fge.github.com/json-schema-validator/stable/index.html">Javadoc</a>).</li>
</ul>

<h2>Maven artifacts</h2>

<p><b>Warning: the artifact and package namespace has changed starting with version 1.5.3</b></p>

<p>In the examples below, substitute <tt>your-version-here</tt> for the version you want.</p>

<h3>Versions 1.5.3, 1.5.4, 1.99.x and future versions</h3>

```xml
<dependency>
    <groupId>com.github.fge</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>your-version-here</version>
</dependency>
```

<h3>Versions 1.5.2 and below</h3>

```xml
<dependency>
    <groupId>org.kitchen-eel</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>your-version-here</version>
</dependency>
```

<h2>Versioning scheme policy</h2>

<p>The versioning scheme is defined by the <b>middle digit</b> of the version number:</p>

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
* draft v3 and draft v4 validation;
* <tt>$schema</tt> detection;
* full schema addressing support;
* validation of numeric JSON instances of arbitrary scale/precision;
* ECMA 262 regexes (using Rhino);
* extensive customization: (un)registering URI schemes, setting namepaces, redirections, registering
  schemas/keywords, others.

<h2>Relevant documents</h2>

<p>This implementation is based on the following drafts:</p>

* <a href="http://tools.ietf.org/html/draft-zyp-json-schema-04">JSON Schema Internet draft, version
  4</a> (<a href="http://tools.ietf.org/html/draft-fge-json-schema-validation-00">link to validation
  spec</a>);
* <a href="http://tools.ietf.org/html/draft-zyp-json-schema-03">JSON Schema Internet draft, version
  3</a>;
* <a href="http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03">JSON Reference Internet draft,
  version 3</a>;
* <a href="http://tools.ietf.org/html/draft-ietf-appsawg-json-pointer-09">JSON Pointer Internet
  draft, version 9</a>.

<h2>More...</h2>

<p>For a detailed discussion of the implementation, see <a
href="https://github.com/fge/json-schema-validator/wiki/Status">here</a>. For a list of planned
features for next versions, see <a
href="https://github.com/fge/json-schema-validator/wiki/Roadmap">here</a>.

Please see the <a href="https://github.com/fge/json-schema-validator/wiki/">wiki</a> for more
details.

