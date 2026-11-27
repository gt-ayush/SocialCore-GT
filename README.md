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

The remapped mod JAR is written to `build/libs/`. Install the matching Polymer distribution on the server. Vanilla clients do not need to install SocialCore-GT; the enhanced P-key screen will be available to clients that install the mod.

On first launch, SocialCore-GT creates `config/socialcore-gt/config.json` with `clientScreenIntegrationEnabled` enabled by default. Set it to `false` on a client to hide the SocialCore entry point from Minecraft's Social Interactions screen. Invalid or unreadable configuration is reported in the log and defaults are used.