# IOTA Integration Services Java SDK
This is the Java SDK for easy usability of the Integration Services API.

## Prerequisites 

* JDK 16 (recommended)
* A reference to an instance of the [Integration Services API](https://github.com/iotaledger/integration-services)
* Maven

## [Examples](https://github.com/albydeca/iota-is-sdk/tree/main/examples)

## Getting Started
Please set up the following files in order to generate the JAR and run the code locally:

- `env.properties` - with the following structure:
  ```
  api-key=XXXXXXX
  api-version=vX.X
  api-url=XXXXXXX
  identity-file=adminIdentity.json
- `adminIdentity.json` - will contain the admin identity object (json file with elements `doc` and `key`)

Then, perform the following steps:
```angular2html
mvn clean install
```
You are now ready to use the JAR and access the classes. Please remember to keep the `env.properties` in the same folder as the JAR.
The JAR can be used as a dependency to run the examples, which, contrary to the node implementation, exist as part of their own package
which depends on this project's JAR (see `examples/pom.xml`)

The IOTA Foundation has created a handy [wiki](https://wiki.iota.org/integration-services/welcome) to help you better understand
the platform's characteristics and usage. A Java-specific section is coming soon. In the meantime, please refer to the
[node](https://wiki.iota.org/integration-services/examples/introduction) section.

A basic `Dockerfile` has been provided to help you run this code in its own container.
  
## Extending this codebase
We have kept this codebase as small and general-purpose as possible, wrapping it in a Maven project to facilitate the
creation of JAR files and libraries. Below are suggested extensions that the Integration Services team and/or future maintainers
could consider implementing:
- Hook this codebase to a MongoDB instance as per the [Node.js](https://github.com/iotaledger/integration-services/tree/master/clients/node) implementation
- Write unit tests
- Gradle support

## Authors
- Project Owner & Maintainer: Alberto De Capitani [[GitHub](https://github.com/albydeca), [Email](mailto:alberto.de-capitani@gradba.se)]
- Lead developer: Giulio Casarotti [[GitHub](https://github.com/tulio98), [Email](mailto:876589@stud.unive.it)]


Special thanks to [Michele Nati](https://www.linkedin.com/in/michelenati),  [Dominic Zettl](https://www.linkedin.com/in/dominic-zettl-35720310a)
and [Michele Mastrogiovanni](https://www.linkedin.com/in/michele-mastrogiovanni/) for their precious contributions.