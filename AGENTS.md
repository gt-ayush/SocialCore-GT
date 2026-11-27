# SocialCore-GT Agent Guide

## Project goal

Build SocialCore-GT as a lightweight Fabric social platform for Minecraft servers. Its primary experience is an enhanced version of Minecraft Java's Social Interactions screen, opened with the configurable P key by default. A server-opened inventory GUI is the fallback for players using vanilla clients.

The P-key screen requires client-side code; a server-only mod cannot replace or modify that screen. Keep social rules, permissions, and authoritative state on the server. Never assume a client-provided action is valid.

## Product and technical constraints

- MVP: social hub, friends, teams, temporary direct messages, and temporary notifications.
- Vanilla clients must be able to join and use the server-opened GUI. The enhanced P-key screen is available to clients with SocialCore-GT installed.
- Use Fabric, Polymer, and SGui where appropriate; verify supported Minecraft/Fabric versions before choosing APIs.
- Store permanent friend relationships and teams as JSON under `config/socialcore-gt/`.
- Do not use an external database.
- Messages and notifications exist only in RAM. Messages expire after 10 minutes; notifications expire after 5 minutes. Clear a player's messages when either participant disconnects, and clear their notifications when they disconnect.
- Load persistent data at startup, cache active state, and save only after changes. Do not access disk every tick; persist asynchronously using safe snapshots and file replacement.
- Use UUIDs as player identity, not usernames.
- Integrate LuckPerms permissions without making it mandatory for the mod to run. Enforce permissions and validation on the server for both GUI and command actions.
- Preserve useful vanilla Social Interactions behavior, including player search and existing safety actions, unless a deliberate product decision says otherwise.
- Keep modules cohesive and avoid adding future systems (kingdoms, guilds, marketplace, cross-server support) to the MVP.

## Implementation phases

Complete phases in order unless a dependency or compatibility finding requires revising the sequence. Each phase should leave the project buildable and include focused tests for new behavior.

### Phase 0 — Resolve compatibility and product decisions

1. **Completed — Version baseline (verified 2026-11-27):**
   - Minecraft Java Edition: `26.3` (initial single-version target).
   - Java: `25` for development and runtime.
   - Fabric Loader: `0.20.1` (stable for Minecraft `26.3`).
   - Fabric API: `0.162.0+26.3`.
   - Polymer: `0.18.7+26.3`.
   - SGui: `2.2.1+26.3`.

   This pins Polymer to the Fabric API version in its `26.3` release and uses SGui's matching `26.3` release. Recheck upstream compatibility before implementation if any pinned release changes.
   Sources: [Minecraft 26.3 Java version](https://minecraft.wiki/w/Java_Edition_26.3), [Fabric Loader versions for 26.3](https://meta.fabricmc.net/v2/versions/loader/26.3), [Fabric API 0.162.0+26.3](https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/0.162.0%2B26.3/fabric-api-0.162.0%2B26.3.pom), [Polymer 0.18.7+26.3 release](https://github.com/Patbox/polymer/releases/tag/0.18.7%2B26.3), [SGui 2.2.1+26.3 build configuration](https://github.com/Patbox/sgui/blob/2.2.1%2B26.3/gradle.properties).
2. **In progress — P-screen integration spike:** Keep Minecraft's existing Social Interactions screen and P key mapping as the entry point; do not register a competing P binding or replace the vanilla screen. On Minecraft `26.3`, Fabric API provides `ScreenEvents.AFTER_INIT` and `Screens.getWidgets`, so the initial approach is to add a SocialCore entry point to `SocialInteractionsScreen` and open the SocialCore hub from there. Keep vanilla player search and report/block controls untouched; provide search within the SocialCore player view and a back path to the vanilla screen. Avoid injecting into private player-row internals in the first iteration. The Fabric API extension points are verified from the [26.3 ScreenEvents API](https://maven.fabricmc.net/docs/fabric-api-0.162.0+26.3/net/fabricmc/fabric/api/client/screen/v1/ScreenEvents.html), [AFTER_INIT callback](https://maven.fabricmc.net/docs/fabric-api-0.162.0+26.3/net/fabricmc/fabric/api/client/screen/v1/ScreenEvents.AfterInit.html), and [Screens widget API](https://maven.fabricmc.net/docs/fabric-api-0.162.0+26.3/net/fabricmc/fabric/api/client/screen/v1/Screens.html); Fabric also documents configurable key mappings [here](https://docs.fabricmc.net/develop/key-mappings).

   **Runtime prototype still required:** During Phase 1, verify the entry point appears once each time the vanilla screen opens or is resized, opens the SocialCore hub, and returns correctly. Verify vanilla search and report/block actions still work, including at the smallest supported GUI scale. If adding a widget proves unreliable on the target screen, use a narrowly scoped client mixin as a fallback and document why before proceeding.
3. **Completed — Packaging and entrypoint strategy:** Build one combined SocialCore-GT mod JAR with Loom's split `main` and `client` source sets. Keep server/common code in `src/main`; keep P-screen UI and client-only mixins in `src/client`. In `fabric.mod.json`, use the `main` entrypoint for common/server initialization and the `client` entrypoint for client UI initialization. Client-only mixin configs must be environment-scoped. Do not reference client classes from common/server initializers or signatures. Install the same JAR on the server and on clients that want the enhanced screen; vanilla clients do not install it and use the server-opened fallback GUI. Only send custom payloads to clients after checking that they support SocialCore-GT's client protocol. This follows Fabric's [environment-specific entrypoint rules](https://docs.fabricmc.net/develop/loader/fabric-mod-json) and the official template's [split source-set configuration](https://github.com/FabricMC/fabric-example-mod/blob/1.21/build.gradle).

   **Phase 1 verification:** Build and launch the dedicated server without loading client classes, launch the client entrypoint, and verify an unmodded vanilla client can connect and use the fallback GUI.
4. **Completed — Vanilla-client fallback GUI:** Provide an SGui/Polymer inventory hub opened by the server command (provisionally `/socialcore`; aliases are finalized in subpart 6). It must work for an unmodded vanilla client and include Online Players, Friends, Teams, Messages, and Notifications pages, with back navigation, pagination, empty states, and server-validated actions. Online player entries can open a context menu for supported friend, message, and team actions; team actions must reflect the viewer's role.

   Inventory menus support selecting from displayed players and options, but do not provide arbitrary text entry or a client-side search box. Use paginated lists for browsing; route free-text operations such as message content, team names, and team chat through documented chat commands. The fallback cannot customize the vanilla P-key screen, add controls to its player rows, or provide client-only widgets. These remain available only in the optional client UI. Teleport requests and ignore-player actions are outside the current MVP.
5. Specify pending friend-request lifetime/persistence, invitation expiry, team ownership transfer/disband rules, and default permissions when LuckPerms is absent.
6. Confirm stable identifiers: mod ID, command roots/aliases, config folder, and permission-node names; decide whether any legacy `pulse.*` permission aliases are needed.

**Exit criteria:** A short, recorded set of compatibility and behavior decisions; a minimal proof that the screen approach and fallback are feasible.

### Phase 1 — Project foundation

1. Set up the Fabric Loom build, mod metadata, source sets/entrypoints, and pinned dependencies.
2. Add server and optional client initialization, reuse Minecraft's configurable Social Interactions key mapping without registering a competing P binding, and keep client/server classes side-separated.
3. Establish package boundaries for commands, GUI, friends, teams, messages, notifications, storage, permissions, and shared utilities.
4. Add logging and a minimal config/defaults mechanism without introducing unnecessary dependencies.
5. Verify the mod starts on a dedicated server and a client, and that vanilla clients can connect.

**Exit criteria:** Reproducible build and successful startup/connectivity smoke tests for the supported environments.

### Phase 2 — Persistence and shared domain foundation

1. Define UUID-based models and validation for friends and teams.
2. Implement versioned JSON loading, missing-file initialization, invalid-data reporting, and migrations where needed.
3. Implement dirty-state tracking and asynchronous snapshot saves with safe file replacement and shutdown flushing.
4. Keep transient data out of storage by design; add tests proving messages and notifications are never serialized.
5. Add a shared server-side service layer so commands and GUIs call the same validated operations.

**Exit criteria:** Restart tests preserve valid permanent data; malformed files produce explicit diagnostics; saves occur only after changes.

### Phase 3 — Friends

1. Implement friend request creation, duplicate/self-request checks, accept, decline, remove, and list operations.
2. Apply the pending-request policy decided in Phase 0.
3. Add permission checks and clear success/error feedback.
4. Add command flows under `/socialcore friend ...`.
5. Test online/offline targets, duplicate requests, reconnect behavior, persistence, and relationship symmetry.

**Exit criteria:** Friend operations work through the service and commands, with permanent relationships surviving restart.

### Phase 4 — Teams

1. Implement team creation, membership invariants, owner/officer/member roles, and roster queries.
2. Implement invitations, acceptance, leaving, kicking, promotion, ownership transfer, and disbanding according to Phase 0 decisions.
3. Implement team chat with server-side membership checks.
4. Add command flows under `/socialcore team ...`.
5. Test role boundaries, one-team-per-player enforcement, offline members, and team persistence.

**Exit criteria:** Team state remains consistent through all membership changes and survives restart.

### Phase 5 — Temporary messages and notifications

1. Implement RAM-only direct-message conversations, unread state, and reply targeting.
2. Expire messages after 10 minutes and remove all conversation data involving a disconnecting player.
3. Implement RAM-only notifications with a 5-minute expiry and recipient-disconnect cleanup.
4. Use bounded scheduled cleanup rather than per-tick disk work or unbounded retained state.
5. Test expiry, disconnect cleanup, restart loss, and unread/notification behavior.

**Exit criteria:** Temporary state is fully ephemeral, expires on schedule, and is never written to disk.

### Phase 6 — Vanilla-client fallback GUI

1. Build the `/socialcore` inventory-based hub using SGui/Polymer.
2. Add online players, friends, teams, messages, and notifications views as their services become available.
3. Add pagination, back navigation, empty states, and clear feedback; use chat commands for operations requiring free-text input.
4. Revalidate permissions, target availability, and action eligibility when each menu action is handled.
5. Test with an unmodded vanilla client and confirm server-authoritative behavior.

**Exit criteria:** Vanilla clients can use the supported MVP social workflows without installing SocialCore-GT.

### Phase 7 — Enhanced P-key Social Interactions screen

1. Implement the client screen integration based on the Phase 0 prototype, with graceful behavior if the screen changes in a future Minecraft version.
2. Provide searchable online players and entry points to profile, friend, message, and team actions.
3. Add Friends, Teams, Messages, and Notifications views with navigation that fits the existing Minecraft UX.
4. Register only the required client-to-server requests and server-to-client updates; validate all requests on the server.
5. Preserve vanilla safety controls and verify that clients without the mod retain normal vanilla behavior.

**Exit criteria:** The configured P key opens the enhanced screen for modded clients; the server remains compatible with vanilla clients.

### Phase 8 — Permissions, integration, and release validation

1. Complete LuckPerms integration for the agreed use, friend, team, message, and admin permission nodes; add legacy `pulse.*` aliases only if Phase 0 requires them.
2. Verify consistent permission behavior across commands, the fallback GUI, and the enhanced screen.
3. Run focused unit/integration tests and a dedicated-server/client compatibility matrix.
4. Review data cleanup, asynchronous save shutdown behavior, logging, and error reporting.
5. Document installation, client/server requirements, commands, permissions, storage, and known compatibility limits.

**Exit criteria:** Build and tests pass; install and fallback instructions clearly distinguish the optional client enhancement from server functionality.

## Working practices

- Inspect existing code and tests before changing behavior; make focused changes and preserve unrelated work.
- Add or update tests with each behavior change. Prefer the smallest relevant test/build command, then run broader validation when needed.
- Do not silently swallow persistence, networking, or permission errors.
- Keep implementation notes and product decisions in this file or relevant project documentation; do not create speculative systems outside the MVP.
