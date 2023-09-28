### Building a Jar

`gradlew jar` / `./gradlew jar`

Output jar should be in `build/libs`.

### Submodules

- `core` is the core server and client
- `packets` some extra packets and utils that can be used in developments
- `test` only for internal use

### TODO

- [ ] Review, clean and improve code structure
- [ ] Review JavaDoc
- [ ] More useful methods
- [ ] Timeout
- [ ] Packet spam detection
- [x] Error event
- [ ] Scheduling tasks

#### Server

- [x] Requests

#### Client

- [x] `EventListener` like in server
- [x] `response` for requests

### Notes

Don't confuse `requestListeners` and `responseListeners`. Here's the difference:

- `requestListeners` Map is used to listen for incoming `RequestPacket`s on Server/Client
- While `responseListeners` are those lambdas used to handle the received back response

