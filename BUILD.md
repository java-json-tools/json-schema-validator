## Preamble

All instructions in this file use the Linux (or other Unix) conventions for
build. If you happen to use Windows, replace `./gradlew` with `gradlew.bat`.

## Building instructions

### Gradle usage

You may be fortunate enough that your IDE has Gradle support. Should it not
be the case, first report a bug to your vendor; then refer to the cheat sheet
below:

```
# List the list of tasks
./gradlew tasks
# Build, test the package
./gradlew test
# Install in your local maven repository
./gradlew clean install
```

If you try and play around with Gradle configuration files, in order to be
_really sure_ that your modifications are accounted for, add the
`--recompile-scripts` option before the task name; for instance:

```
./gradlew --recompile-scripts test
```

### Note about testing

When you invoke Gradle tasks such as `install`, for instance, Gradle will _not_
run tests by default; you therefore have to tell it to run tests explicitly:

```
./gradlew clean test install
```

The same holds true for the `jar` target, for instance.

## Note to Maven users

There exists a possiblity to generate a `pom.xml` (using `./gradlew pom`), which
is there for convenience. However, this is not supported by the author.

