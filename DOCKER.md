# README

Here we show how to run examples using docker.

In the main directory run:

```
docker build . -t gradbase-iota-is-sdk
```

Copy the following files in `examples`:

- `env.properties` - with the following structure:
  ```
  api-key=XXXXXXX
  api-version=vX.X
  api-url=XXXXXXX
  identity-file=adminIdentity.json
- `adminIdentity.json` - will contain the admin identity object (json file with elements `doc` and `key`)

Run this Docker command to start the first example:

```
docker run \
    -v $(pwd)/examples:/examples \
    -w /examples \
    gradbase-iota-is-sdk:latest \
        mvn compile exec:java \
            -Dexec.mainClass="net.gradbase.examples.CreateIdentityAndCredential"
```