<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file
src/main/resources/LICENSE for the full text.</p>

<p><b>IMPORTANT:</b> work is under way for version 1.0, and in 1.0, some format
specifiers are split from this package and are in a new one:
[json-schema-formats](https://github.com/fge/json-schema-formats). Support for
the following format specifiers are not in <tt>master</tt> anymore:</p>

* <tt>date</tt>,
* <tt>time</tt>,
* <tt>phone</tt>,
* <tt>utc-millisec</tt>,
* <tt>date-time-ms</tt>.

<p>Support for other format specifiers are still in this package for various
reasons, see the above project for further explanations.</p>

<h2>Versions</h2>

<p>The current version is <b>0.6.0</b>. The big change in that version is the
newer validation message format. See
[here](https://github.com/fge/json-schema-validator/wiki/Validation-messages)
for more details.</p>

<p>The old stable version is <b>0.5.5</b>. See the
[ChangeLog](https://github.com/fge/json-schema-validator/wiki/ChangeLog) for
more details. <b>Versions 0.4.x are not supported anymore.</b></p>

<p>If you want to jump straight into action, you can see the Javadoc online
[here](http://fge.github.com/json-schema-validator/), which contains code
samples.</p>

<h2>What it is</h2>

<p>This is an implementation of the [JSON Schema
specification](http://json-schema.github.com/json-schema/) written in pure Java.
This allows pure server side JSON schema validation if this is what you are
looking for.<p>

<p>This implementation has the following features:</p>

* full draft v3 validation (however, see above for <tt>format</tt>);
* arbitrary length/precision number validation;
* ECMA 262 regexes (using Rhino);
* full <tt>$ref</tt> support, including <tt>id</tt> resolving and loop
  detection;
* thread safe validators;
* ability to register/unregister URI handlers for arbitrary URI schemes
  (natively supported: <tt>http</tt>, <tt>ftp</tt>, <tt>file</tt> and
  <tt>jar</tt>);
* ability to register/unregister keywords;
* [very fast](https://github.com/fge/json-schema-validator/wiki/Performance).

<h2>Relevant documents</h2>

<p>Currently, JSON Schema is not an official RFC. In fact, the existing draft is
obsolete -- but it is used on the Internet nonetheless. This implementation is
based on the following drafts:</p>

* JSON Schema Internet draft, version 3
  ([link](http://tools.ietf.org/html/draft-zyp-json-schema-03));
* JSON Reference Internet draft, version 2
  ([link](http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-02));
* JSON Pointer Internet draft, version 3
  ([link](http://tools.ietf.org/html/draft-ietf-appsawg-json-pointer-03)).

<h2>More...</h2>

<p>For a detailed discussion of the implementation, see
[here](https://github.com/fge/json-schema-validator/wiki/Status). For a list of
planned features for next versions, see
[here](https://github.com/fge/json-schema-validator/wiki/Roadmap).

Please see the [wiki](https://github.com/fge/json-schema-validator/wiki/) for
more details.

