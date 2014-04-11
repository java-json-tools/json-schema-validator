### 2.1.10

* Plug in hyperschema syntax support...
* Gradle 1.11.
* Add a main method.
* -core 1.1.11.

### 2.1.9

* Fix bug with string length calculations: it is the number of Unicode code
  points which matters, not the number of `char`s (issue #92).
* Depend on -core 1.1.10: schema sources with trailing input are now considered
  illegal.
* Make all tests run from the command line.
* Small javadoc fixes.

### 2.1.8

* Add "deep validation": validate children even if container fails
* -core update to 1.1.9: package changes (BREAKS OLDER CODE)
* Change licensing to dual LGPLv3/ ASL 2.0
* Dependencies updates (Joda Time 2.3, libphonenumber 5.9)

### 2.1.7

* Import all format attributes from
  [json-schema-formats](https://github.com/fge/json-schema-formats).
* Switch to gradle for build.
* Major dependencies updates; drop ServiceLoader for message bundles.
* Fix javadoc generation.

### 2.1.6

* Update json-schema-core dependency to 1.1.7.
* Fix a bug in pom.xml which would cause the service file to not be generated.
* Fix two places where core messages would not be fetched properly.

### 2.1.5

* Update json-schema-core dependency to 1.1.6.
* Use [msg-simple](https://github.com/fge/msg-simple) for message bundles.
* Remove all unchecked exceptions, now unneeded.
* Improve, test all error messages.
* pom.xml improvements.
* Remove obsolete code.

### 2.1.4

* Update -core dependency.
* Use resource bundles for all configuration/validation messages (issue #55).
* Modify pom.xml to allow OSGi-capable deployments (courtesy of Matt Bishop).

### 2.1.3

* Modify date/time format checkings to accurately check for the required number
  of digits

### 2.1.2

* Update -core dependency to 1.1.3
* Update libphonenumber dependency

### 2.1.1

* Update -core dependency (including Guava), adapt code.
* Some error message rework.

### 2.1.0

* Depend on -core 1.1.1. Change relevant code accordingly.
* Simplify failure code on syntax validation failure.
* Fix `date-time` format checking: up to three millisecond digits are allowed by
* ISO 8601.
* Joda Time dependency updated to 2.2.

