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


#### Server

- [x] Requests

#### Client

- [ ] `EventListener` like in server
- [x] `response` for requests
