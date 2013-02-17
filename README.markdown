<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.</p>

<p>This implementation has complete validation support for the latest draft (v4) and the previous
draft (v3). More generally, it has quite an <a
href="https://github.com/fge/json-schema-validator/wiki/Features">extensive list of features</a>.<p>

<p>Should you wonder about it, this library is reported to <a
href="http://stackoverflow.com/questions/14511468/java-android-validate-string-json-against-string-schema">work
on Android</a> :)</p>

<p>Quick start:</p>

<ul>
    <li>you can <a href="http://json-schema-validator.herokuapp.com">test this library online</a>;
    you can even <a href="https://github.com/fge/json-schema-validator-demo">fork this application
    and run it yourself</a>;</li>
    <li>you can have a look at the <a
    href="http://fge.github.com/json-schema-validator/stable/index.html?org/eel/kitchen/jsonschema/examples/package-summary.html">code
    samples</a> (note: stable version).</li>
</ul>


<p>This project also has a dedicated <a
href="https://groups.google.com/forum/?fromgroups#!forum/json-schema-validator">Google
group</a>.</p>

<h2>Current status</h2>

<p>Work is underway to get this library up to version 2.0.x. This implies <a
href="https://github.com/fge/json-schema-validator/wiki/Roadmap">some profound changes to its
core</a>.

<p>In essence: with the new core, it will become possible to do whatever you want with your JSON
data. And that includes generating JSON Schema from POJOs, or the reverse.</p>

<p>At the time of this writing, the core processing architecture has reached a workable enough state
that it can already be split apart, but it lacks documentation and examples.

<p>Furthermore, the primary objective is to provide a user API to give as much, or more, control,
over all validation aspects than what 1.x allowed: as of 1.99.6, the core components are there, but
the user interface is 80% there.</p>

<h2>Versions</h2>

<ul>
    <li>development version: <b>1.99.9</b>; (<a
    href="https://github.com/fge/json-schema-validator/wiki/ChangeLog.devel">ChangeLog</a>). As the
    Javadoc is not done yet, no links to it, sorry!</li>
    <li>stable version: <b>1.6.1</b> (<a
    href="https://github.com/fge/json-schema-validator/wiki/ChangeLog.stable">ChangeLog</a>, <a
    href="http://fge.github.com/json-schema-validator/stable/index.html">Javadoc</a>).</li>
</ul>

<h2>Maven artifact</h2>

<p>In the example below, substitute <tt>your-version-here</tt> for the version you want.</p>

```xml
<dependency>
    <groupId>com.github.fge</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>your-version-here</version>
</dependency>
```

<h2>Versioning scheme policy</h2>

<p>The versioning scheme is defined by the <b>middle digit</b> of the version number:</p>

* if this number is <b>even</b>, then this is the <b>stable</b> version; no new features will be
  added to such versions, and the user API will not change (save for some additions if requested).
* if this number is <b>odd</b>, then this is the <b>development</b> version; new features will be
  added to those versions only, <b>and the user API may change</b>.

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
href="https://github.com/fge/json-schema-validator/wiki/Status">here</a>.</p>

<p>Please see the <a href="https://github.com/fge/json-schema-validator/wiki/">wiki</a> for more
details.</p>

