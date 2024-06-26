# Bikeshed -> Event Sourcing Demo App

This is a demo app for a presentation I gave at [Spring I/O 2024](https://2024.springio.net/) on Event Sourcing. 
This application is meant to illustrate how a Spring Application can be made to be Event Sourced. It is very much a toy 
application and serves no real business purpose, but hopefully can be used as a reference for those out there who are 
curious on the basics of how to implement an Event Sourced application. 

## Getting Started and Requirements

* java 21
* a recent version of docker

## Running the application

1. Start the database: `docker compose up`. Note that it runs on a separate port from the test db.
2. Run the migrations: `./gradlew flywayMigrate`
3. Generate the JOOQ classes: `./gradlew jooqGenerate`
4. Run the application: `./gradlew bootRun`

#### Tests

1. Run the basic app first (or at least up to generating JOOQ classes)
2. Run the tests: `./gradlew test`


## Callouts - What to Look at!

I'll attempt to call out various aspects of what I've laid out 
here, and a brief mention of why I believe it's important. Further discussion and comments can be found within key 
objects.

First, this system is split into multiple modules. It follows a Clean Architecture model (I also [gave a talk on this](https://www.youtube.com/watch?v=mbNzUkNjrnA) if
you'd like more background). The modules are as follows:

* `bs-core` - The core "business" logic, entities, and events
* `bs-detail` - Implementation of 'the details', e.g. technology-specific adapters. In this case, mostly Postgres and Jooq.
* `bs-app` - Where Spring Boot application lives and is the place of 'integration' of the other modules. Configuration, http layer, and integration tests.
* `eventsource` - What you're here for

At first glance, this multi-module project structure is arguably a bit overkill for this demo. However, it also 
demonstrates additional "clean architecture" practices, like handling transactions across multiple objects. While the 
sheer number of interfaces can get in your way as your browse the codebase, the idea I'm attempting to underline with 
this approach is that: the persistence and retrieval of our events is just another implementation detail.

> Events live in the Core of our system because they are core to our business logic.

### General Architecture And Flow

The core of the system are the relation between the `EntityStore`, `Event Repository`, and the `ProjectionOrchestrator`.

As events are created and persisted, they are passed to the `EntityStore`. Within a single transactional boudnary, 1) the 
events are written to the Journal through the `EventRepository` AND then 2) each event (envelope) is passed to synchronous
projections through the `ProjectionOrchestrator`. After that transaction completes, the `ProjectionOrchestrator` will 
hand off the events to asynchronous projections in an eventually consistent manner.

This is meant to be an example, in a real application you'd likely want to do something a bit less clunky than the 
ProjectionOrchestrator as implemented here, but rather use an Observer pattern where Projection models could register through
a central dispatcher for event envelopes after they're persisted.

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

One interesting aspect of writing to an append-only journal is that multiple individual "updates" (or what would otherwise be
updates in a non-ES system) can be handled in a single write transaction. This will almost certainly be highly performant, particularly
compared with an equivalent update batch. This may not be immediately obvious looking at the `JooqEventRepository`, but 
the [EntityStore](bs-detail/src/main/kotlin/com/pember/bikeshed/sql/JooqEntityStore.kt) - a convenience class for handling the peristence of events + passing them on to Query Models - 
demonstrates how this can be done.

Similarly, loading multiple, different entities, is simply a matter of instantiating empty ones, fetching their events,
and passing them to the instances using a method similar to a `group by`. This can be seen in the abstract [EntityStore](bs-core/src/main/kotlin/com/pember/bikeshed/core/common/EntityStore.kt) class.

#### Projection Orchestration

The [ProjectionOrchestrator](bs-core/src/main/kotlin/com/pember/bikeshed/core/projections/ProjectionOrchestrator.kt) is a rather cludgy or ham-fisted 
demonstration of how to route events into relative mechanisms to handle updating projections. It used by the `EntityStore`
after persisting events, and can differentiate between "constraint projections" which need to be updated in the same transaction
as the events (as we've chosen an RDBMS for our event store), and "async projections / query models", where it is acceptable to 
be eventually consistent.

#### Replayer

The act of "replaying" events is vital for a variety of use cases, particularly regarding model changes, rebuilding data stores, error recovery, and the like. 
A somewhat trivial example of how to do this is located in the [EventReplayer](bs-detail/src/main/kotlin/com/pember/bikeshed/replay/EventReplayer.kt) class.

#### TODO?


