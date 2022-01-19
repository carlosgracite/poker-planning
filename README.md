# Planning Poker Challenge

A simple Android application that permits remote teammates to estimate user stories in realtime.

[![Demo Video](/preview/poker-demo-image.png)](http://www.youtube.com/watch?v=h2HGbNNOdR4)

## Tools used

- [Kotlin](https://kotlinlang.org) language;
- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
  and [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow)
  for asynchronous tasks;
- [Hilt](https://dagger.dev/hilt) for dependency injection;
- Jetpack Compose for UI;
- Jetpack ViewModel to hold screen related data and logic;
- [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for
  simple data storage;
- [Compose Accompanist](https://github.com/google/accompanist) for simpler handling of screen
  insets;
- [OkHttp3](https://github.com/square/okhttp) as HTTP client;
- [Retrofit2](https://github.com/square/retrofit) for REST API;
- [Scarlet](https://github.com/Tinder/Scarlet) to simplify WebSocket communication;
- [Moshi](https://github.com/square/moshi/) for JSON serialization/deserialization;
- [Timber](https://github.com/JakeWharton/timber) to extend platform Log capabilities;
- [Turbine](https://github.com/cashapp/turbine) to simplify testing of kotlin Flows;
- [Mockito](https://site.mockito.org) to create mocks for some unit tests;

## Architecture

An MVVM approach was used to organize application structure:

- A Jetpack ViewModel his responsible to hold UI state and handle screen logic/events;
- The UI state is represented by a single immutable object which is updated by the ViewModel;
- State changes are communicated to the UI through a `StateFlow` stream;
- A single `sealed class` is used to represent all events that a ViewModel can handle;
- Navigation is done by simply changing composables based on application state;
- Application state and business logic is handled through `UseCase` classes. They follow the
  convention of only executing a single business rule and should be stateless.

## Backend

A very simple backend service (using [Spring Boot](https://spring.io/projects/spring-boot)) has been
created to provide the communication between users. It supports the following WebSocket messages:

Most of the communication is done through a WebSocket channel. These are the message types that the
client can send:

- `start-session` for the client to indicate that it wants to receive room updates;
- `leave-room` which removes the user from a planning room;
- `vote` to send the story point for the what is currently being voted;
- `reveal-results` to reveal the voting results when at least one user has voted;
- `start-voting` to open the session for voting.

Room updates are broadcasted to all users that have joined in it through the `room-update` event
type.

A REST API is used to create or join a room:

- `POST /api/room`
- `POST /api/room/{roomId}/user`

NOTICE: All session/user data stays in-memory and it is hosted in a free Heroku server. Also, the
websocket connection is not fully configured, so major instabilities are expected for now.

## // TODO

- Improve way to share room id (share intent, QR code, etc);
- Support multiple planning poker decks (currently only t-shirt sizes);
- Save previous voting results;
- Use KMM to share logic between Server/App (and why not create iOS/Web versions?);
- Create a proper backend service xD.

# License

```xml
Copyright 2020 Carlos Amaral

    Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with the License.You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, softwaredistributed under the License is distributed on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.See the License for the specific language governing permissions andlimitations under the License.
```