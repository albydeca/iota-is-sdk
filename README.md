# InteractionIOTA
This package provides a basic java-based interaction mechanism with [IOTA Streams](https://www.iota.org/solutions/streams) 
[API](https://ensuresec.solutions.iota.org/docs/).

With this package, you can learn more about the principal components of Streams, including **Creators**, **Auditors** and **Channels**.
You can also perform basic tasks such as create and save channels, read and write data to channels, and authenticate to channels.

We intend this codebase to constitute a building block for other developers to build Java applications on IOTA Streams. To this end,
we have conveniently wrapped the codebase in a [Maven](https://maven.apache.org/) project. [Gradbase](https://www.gradba.se) are
using this codebase to create a PubSub system for COVID-19 EU GreenPass verification keys.

## Getting Started
To make the most of the full feature set, please add the following two folders to the root of the project:

- `persistent_channels/` - this will contain JSON files that map `LogCreator`s to `Channel`s (one-to-one)
- `data_source/` - will contain the data files (JSON format) to upload to the channel

Then, perform the following steps:
```angular2html
mvn install
mvn compile
```
You are now ready to run the `MainClass`:
`java -Dapi-key=<<your-iotastreams-api-key>> -Ddata-filepath=<<data_source/XXX.json>> -Dpersist_channel=<<true/false>> MainClass`

### A note on arguments
- `data-filepath` can be either absolute or relative and must reference a JSON file. JSON arrays are not yet supported in
IOTA Streams.
  
- `persist-channel` will make sure that the channel ID created by a given `LogCreator` will get saved to a file for future
usage, and will load and use such an ID from file if one has been previously saved for a given `LogCreator`. If this flag
  is set to `false`, neither of these behaviours occur. Instead, a session-specific channel is created and its ID does not gets
  saved anywhere. At the moment, the repo supports one ID per `LogCreator` saved in a JSON file.
  
## Extending this codebase
We have kept this codebase as small and general-purpose as possible, wrapping it in a Maven project to facilitate the
creation of JAR files and libraries. For example, extensions to this primitive behaviour could be:
- Hook this codebase to a database backend to persist state
- Write unit tests (planned)
- Write methods to renew JWT upon expiry (planned). At the moment, one JWT per connection to the channel is created. This is
  inefficient and not suitable for high-performance applications.
- Create a JAR library to provide a code library that is agnostic to the backend used to store state.

## Authors
- Lead developer: Giulio Casarotti [[GitHub](https://github.com/tulio98), [Email](mailto:876589@stud.unive.it)]
- Project Owner & Maintainer: Alberto De Capitani [[GitHub](https://github.com/albydeca), [Email](mailto:alberto.de-capitani@gradba.se)]

Special thanks to [Michele Nati](https://www.linkedin.com/in/michelenati) and [Dominic Zettl](https://www.linkedin.com/in/dominic-zettl-35720310a) for their precious contributions.