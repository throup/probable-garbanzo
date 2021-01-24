# probable-garbanzo
> _"What a wonderful phrase!"_

OK... maybe not as catchy as _Hakuna Matata_. But this is a little project to explore some concepts using Scala.

## About the project
In order to enhance my knowledge of Scala, I am trying to implement a simple setup to find prime numbers, making use of
different technologies including gRPC/protobuf.

* [Full project brief](docs/brief.md)
* [Architecture and design decisions](docs/architecture.md)

## Project structure
There are five main components to the project:
* [`prime-number-server`](prime-number-server): a small server which can tell you the next prime number which is greater
  than an input integer.
* [`proxy-service`](proxy-service): a small server which acts as a proxy to `prime-number-server`, returning a stream
  (over HTTP 1.1) of primes up to a given limit.
* [`protobuf`](protobuf): protobuf contract for gRPC communication between the two servers
* [Integration tests](integration): some simple test case scenarios, exercising the two servers together
* [Documentation](docs): notes and documentation about the project

## How to run
This is a [Scala](https://www.scala-lang.org/) project, intended to be built with [SBT](https://www.scala-sbt.org/).
I assume you already have a working Scala development environment. If not,
[let me google that for you](https://lmgtfy.app/?q=scala+development+environment)...

### Run local servers
Both servers can be launched from sbt.

#### prime-number-server
```bash
# run prime-number-server with default options
sbt service/run
```

```bash
# configure available options
PRIME_SERVICE_HOST="service" \ # address to bind to
PRIME_SERVICE_PORT: 9876 \     # port to bind to
sbt service/run
```

#### proxy-service
```bash
# run proxy-service with default options
sbt proxy/run
```

With the default options, the proxy server can be reached at http://localhost:8080/prime/123 (replace `123` with the desired target).

```bash
# configure available options
PRIME_SERVICE_HOST="service" \ # address to contact prime-number-server
PRIME_SERVICE_PORT: 9876 \     # port to contact prime-number-server
PRIME_PROXY_HOST: "proxy" \    # address to bind to
PRIME_PROXY_PORT: 8765 \       # port to bind to
sbt proxy/run
```

### Docker
It is possible to build docker images for both servers:
```bash
# just prime-number-server
sbt service/docker/publishLocal

# just proxy-service
sbt proxy/docker/publishLocal

# both components (shorthand for the two above commands)
sbt dockerBuild
```

Once the images have been built, they can be deployed to any Docker-based environment. A convenience
[Docker Compose](docker-compose.yml) config is provided in the project.
```bash
# bring up docker network...
docker-compose up

# ...and bring it down again
docker-compose down
```

As a convenient shorthand, an SBT task has been defined to build both containers and then bring up the docker network:
```bash
# build docker images and bring up docker network
sbt dockerRun
```
