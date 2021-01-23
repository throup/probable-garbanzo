# The brief
Develop a set of 2 small services that work together to deliver a sequence
of prime numbers up to a given number.

## Description

### Proxy-service

The `proxy-service` acts as an entry point to the outside world.

It's main tasks are:
* expose a HTTP endpoint over REST responding to `GET /prime/<number>` that
continuously streams all prime numbers up to a given <number> e.g.
/prime/17 should return 2,3,5,7,11,13,17.
* delegates the actual calculation to the second microservice via a
Finagle-Thrift OR gRPC RPC call
* handles wrong inputs in a proper way

### Prime-number-server
The `prime-number-server` does the actual Prime number calculation -
it serves responses continuously over Finagle OR gRPC and
uses proper abstractions to communicate failure

### Deliverables
There are three deliverables necessary to complete the task
* proxy-service
* prime-number-server
* thrift OR protobuf contracts used for communication between the two
services

### Requirements
* Language of implementation: Scala OR Java
* Communication between - Finagle-Thrift OR gRPC
* Basic scenario test cases
* Proper commit history
* README describing implementation choices and preferences