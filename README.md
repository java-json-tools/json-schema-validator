## Read me first

This project, as of version 2.1.8, is licensed under both LGPLv3 and ASL 2.0.
See file LICENSE for more details. Versions 2.1.7 and lower are licensed under
LGPLv3 only.

This implementation has complete validation support for the latest draft (v4) and the previous draft
(v3). More generally, it has quite an [extensive list of
features](https://github.com/fge/json-schema-validator/wiki/Features).

Should you wonder about it, this library is reported to [work on
Android](http://stackoverflow.com/questions/14511468/java-android-validate-string-json-against-string-schema)
:)

### Google Group

This project has a dedicated [Google
group](https://groups.google.com/forum/?fromgroups#!forum/json-schema-validator). For any questions
you have about this software package, feel free to post! The author (me) will try and respond in a
timely manner.

### Testing online

You can [test this library online](http://json-schema-validator.herokuapp.com); this web site is in
a [project of its own](https://github.com/fge/json-schema-validator-demo), which you can fork and
run by yourself.

### Command line

Since 2.1.10, there is a command line application. For this, download the standalone jar from
[Bintray](https://bintray.com/fge/maven/json-schema-validator/view) and run it. There are different
options. Here is the output of `--help`:

```
$ java -Done-jar.silent=true -jar json-schema-validator-2.1.10-standalone.jar --help
Syntax: java -jar jsonschema.jar [options] file [file...]

Options: 
    -s, --brief: only show validation status (OK/NOT OK)
    --help: show this help
    -q, --quiet: no output; exit with the relevant return code (see below)
    --syntax: check the syntax of schema(s) given as argument(s)

Exit codes:
    0: validation successful;
    1: exception occurred (appears on stderr)
    2: command line syntax error (missing argument, etc)
    100: one or more file(s) failed validation
    101: one or more schema(s) is/are invalid
```

(note that `-Done-jar.silent=true` is needed for silent output; this is due to a bug in OneJar which
is not silent by default; see [here](https://github.com/rholder/gradle-one-jar/issues/15) for more
details)


## Versioning scheme policy

The versioning scheme is defined by the **middle digit** of the version number:

* if this number is **even**, then this is the **stable** version; no new features will be
  added to such versions, and the user API will not change (save for some additions if requested).
* if this number is **odd**, then this is the **development** version; new features will be
  added to those versions only, **and the user API may change**.

## Versions

* development version: **2.1.10**
  ([ChangeLog](https://github.com/fge/json-schema-validator/wiki/ChangeLog.devel),
  [Javadoc](http://fge.github.io/json-schema-validator/devel/index.html), [code
  samples](http://fge.github.io/json-schema-validator/devel/index.html?com/github/fge/jsonschema/examples/package-summary.html)).
* stable version: **2.0.3**
  ([ChangeLog](https://github.com/fge/json-schema-validator/wiki/ChangeLog.stable),
  [Javadoc](http://fge.github.io/json-schema-validator/stable/index.html), [code
  samples](http://fge.github.io/json-schema-validator/stable/index.html?com/github/fge/jsonschema/examples/package-summary.html)).

Since version 2.1.5, this package is available on
[Bintray](https://bintray.com/fge/maven/json-schema-validator).

## Gradle/Maven artifact

Gradle:

```groovy
dependencies {
    compile(group: "com.github.fge", name: "json-schema-validator", version: "yourVersionHere");
}
```

Maven:

```xml
<dependency>
    <groupId>com.github.fge</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>your-version-here</version>
</dependency>
```

## Extending usage beyond validation

This library's dependency on [json-schema-core](https://github.com/fge/json-schema-core) opens up
many possibilities for extensions. A [dedicated
project](https://github.com/fge/json-schema-processor-examples) already has a few examples.

The [site mentioned above](http://json-schema-validator.herokuapp.com) already has a few examples of
what is possible. Much more is doable. In fact, the only limiting factor is your own imagination!

## Relevant documents

This implementation is based on the following drafts:

* [JSON Schema Internet draft, version 4](http://tools.ietf.org/html/draft-zyp-json-schema-04)
  ([link to validation spec](http://tools.ietf.org/html/draft-fge-json-schema-validation-00));
* [JSON Schema Internet draft, version 3](http://tools.ietf.org/html/draft-zyp-json-schema-03);
* [JSON Reference Internet draft, version
  3](http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03);
* [JSON Pointer (RFC 6901)](http://tools.ietf.org/html/rfc6901).

## More...

For a detailed discussion of the implementation, see
[here](https://github.com/fge/json-schema-validator/wiki/Status).

Please see the [wiki](https://github.com/fge/json-schema-validator/wiki/) for more
details.

