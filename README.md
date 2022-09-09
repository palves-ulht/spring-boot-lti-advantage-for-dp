# Spring Boot LTI Advantage library for Drop Project

This is a fork of the spring boot lti advantage library, with no source code modification, just for convenience, to be used by Drop Project.

* Generates a single jar with all the stuff related to lti. The original library depends on 3 other libraries.
* Includes stuff related to jwks, copied from the spring-boot-lti-advantage-jwks. I could have included this library, but it was provoking bean conflicts.
* It is prepared to publish the jar into the official nexus repository, instead of github packages. This way, developers don't need to go through the hassle of creating a settings.xml with their personal github token.
