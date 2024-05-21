# Bikeshed demo app

This is a demo app for a presentation I gave at [Spring I/O 2024](https://2024.springio.net/) on Event Sourcing. 
This application is meant to illustrate how a Spring Application can be made to be Event Sourced. It is very much a toy 
application and serves no real business purpose, but hopefully can be used as a reference for those out there who are 
curious on the basics of how to implement an Event Sourced application. 

## Getting Started and Requirements


## Running the application

#### Tests

#### Running the application

If you just want to run the application, note that it requires an active local postgres instance, which is handled here 
via docker compose. So:

* `docker compose up` - This will start the db
* `./gradlew bootRun` - run  the app, which should execute flyway and jooq Generate as part of it.


#### Running the Tests
Due to JOOQ auto generation, you'll want to ensure that you have a running db after a `clean` operation. This is most 
easily handled by having the 'dev' db running, even if tests are happening. So:

* `docker compose up` - This will start the db
* `./gradlew flywayMigrate` - Optional: This will run the migrations (this will also occur on app startup, but run this if only using tests)
* `./gradlew jooqGenerate` - generate jooq objects from the db schema. 
* `./gradlew test` - Test the application



## Callouts - What to Look at!

Now this is what you should really be looking at. Here I'll attempt to call out various aspects of what I've laid out 
here, and a brief mention of why I believe it's important. Further discussion and comments can be found within key 
objects.

First, this system is split into multiple modules. It follows a Clean Architecture model (I also [gave a talk on this](https://www.youtube.com/watch?v=mbNzUkNjrnA) if
you'd like more background). The modules are as follows:

* `bs-core`
* `bs-detail`
* `bs-app`
* `eventsource` - What you're here for


### Sections to dive into

#### Implementation of the Event Repository

The Event Repository is the main adapter between the `core` and the EventStore, in this case a Postgres database. 
A glance at the [JooqEventRepository](bs-detail/src/main/kotlin/com/pember/bikeshed/sql/JooqEventRepository.kt) should show just how straightforward it is to interact with our journal at a basic
level. In other words, we have not implemented Snapshotting nor querying by time or by event type. 

#### Event Registration and Aliasing

As shown in the `JooqEventRepository` our system detects all `Event` classes on startup and stores the mappings in a tool 
called the [EventRegistry](eventsource/src/main/java/com/pember/eventsource/EventRegistry.java). This in turn used by the
`JooqEventRepository` to grab the event type and the class when storing and loading events.

The system supports (and encourages) the use of `@EventAlias` annotations on the Event classes as way to denote specific
strings to be used when storing the event, otherwise the class name is used (which is almost never what you want).

#### Reading and writing multiple Entities



#### Projection Orchestration

#### Replayer

#### ?
