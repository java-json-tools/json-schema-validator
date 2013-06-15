### 2.1.4

* Update -core dependency.
* Use resource bundles for all configuration/validation messages (issue #55).
* Modify pom.xml to allow OSGi-capable deployments (courtesy of Matt Bishop).

### 2.1.3

* Modify date/time format checkings to accurately check for the required number
* of digits

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

