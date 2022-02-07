# IOTA Integration Services Java SDK
This is the Java SDK for easy usability of the Integration Services API.

## Prerequisites 

* JDK 16 (recommended)
* A reference to an instance of the [Integration Services API](https://github.com/iotaledger/integration-services)
* Maven

## [Examples](https://github.com/albydeca/InteractionIOTA/tree/main/src/main/java/examples)

## Getting Started
Please set up the following files in order to run the code locally:

- `env.properties` - with the following structure:
  ```
  api-key=XXXXXXX
  api-version=vX.X
  api-url=XXXXXXX
  identity-file=adminIdentity.json
- `adminIdentity.json` - will contain the admin identity object (json file with elements `doc` and `key`)

Then, perform the following steps:
```angular2html
mvn install
mvn compile
```
You are now ready to run the `MainClass` from the root dir:
`java MainClass.java`

This is configured to run the entirety of the examples (which can be seen as a "light" form of test cases) in the
suggested order. One can comment and uncomment as appropriate. We may look at making this process of running the examples
more mature in the future.

The examples follow the exact logics of the Node.js library. Therefore, you can click on the "Examples" link above to
learn more.
  
## Extending this codebase
We have kept this codebase as small and general-purpose as possible, wrapping it in a Maven project to facilitate the
creation of JAR files and libraries. Below are suggested extensions that the Integration Services team and/or future maintainers
could consider implementing:
- Hook this codebase to a MongoDB instance as per the [Node.js](https://github.com/iotaledger/integration-services/tree/master/clients/node) implementation
- Write unit tests
- Push to MVNRepository
- Gradle support

## Authors
- Lead developer: Giulio Casarotti [[GitHub](https://github.com/tulio98), [Email](mailto:876589@stud.unive.it)]
- Project Owner & Maintainer: Alberto De Capitani [[GitHub](https://github.com/albydeca), [Email](mailto:alberto.de-capitani@gradba.se)]

Special thanks to [Michele Nati](https://www.linkedin.com/in/michelenati),  [Dominic Zettl](https://www.linkedin.com/in/dominic-zettl-35720310a)
and [Michele Mastrogiovanni](https://www.linkedin.com/in/michele-mastrogiovanni/) for their precious contributions.