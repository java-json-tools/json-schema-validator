#!/bin/bash

#
# This will build everything that is needed. The only thing it does not do is
# upload artifacts to the server.
#

mvn javadoc:jar source:jar package gpg:sign repository:bundle-create
