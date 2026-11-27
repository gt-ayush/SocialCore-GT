# SocialCore-GT

A lightweight social platform for Fabric Minecraft servers.

## Development setup

- JDK 25
- Minecraft 26.3
- Fabric Loader 0.20.1 or newer
- Fabric API 0.162.0+26.3
- Polymer Core 0.18.7+26.3 (server runtime dependency)
- SGui 2.2.1+26.3 (bundled in the SocialCore-GT artifact)

Build the mod with the Gradle wrapper:

```powershell
.\gradlew.bat build
```

The remapped mod JAR is written to `build/libs/`. Install the matching Polymer distribution on the server. Vanilla clients do not need to install SocialCore-GT; the enhanced P-key player-row actions will be available to clients that install the mod.

On first launch, SocialCore-GT creates `config/socialcore-gt/config.json` with `clientScreenIntegrationEnabled` enabled by default. Set it to `false` on a client to hide SocialCore actions in Minecraft's Social Interactions player list. Invalid or unreadable configuration is reported in the log and defaults are used.

Permanent friendships and teams are stored as versioned JSON in `config/socialcore-gt/friends.json` and `teams.json`. The files are loaded when a server starts and initialized with empty version 1 data when absent. Invalid or unsupported data is reported and prevents the server from starting with partial state; messages and notifications are not part of these files.

The current client integration is an early prototype: a row's Social actions control opens a placeholder player screen. Friend, team, and messaging actions are not implemented yet.