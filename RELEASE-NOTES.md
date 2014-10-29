### 2.2.6

* Fix issue #112: fix circular validation detection; use a "validation stack" to
  detect and spot those situations.
* Depend on -core 1.2.5.
* Update dependencies for libphonenumber, jsr305.

### 2.2.5

* Fix issue #102: detect, and fail on, circular validation.
* Simplify ValidationProcessor.
* Remove one-jar generation; the -lib jar now includes Main-Class.

### 2.2.4

* Add a "lib" target to the build.
* Issue #99: append syntax errors when throwing an InvalidSchemaException.
* Issue #100: attempt to load resources from the context classloader if loading
  from JsonLoader.class fails.

### 2.2.3

* Re-release... 2.2.2 was compiled with JDK 8 :/

### 2.2.2

* Depend on -core 1.2.1 to work around Rhino bug with other packages sealing the
  context.

### 2.2.1

* Main now uses current working directory as default URI namespace.

### 2.2.0

* New major release.

