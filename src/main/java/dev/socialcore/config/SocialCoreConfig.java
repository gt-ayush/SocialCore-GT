package dev.socialcore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dev.socialcore.SocialCoreGt;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SocialCoreConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("socialcore-gt")
            .resolve("config.json");

    private boolean clientScreenIntegrationEnabled = true;

    private SocialCoreConfig() {
    }

    public static SocialCoreConfig load() {
        if (Files.notExists(CONFIG_PATH)) {
            SocialCoreConfig defaults = new SocialCoreConfig();
            try {
                Files.createDirectories(CONFIG_PATH.getParent());
                Files.writeString(CONFIG_PATH, GSON.toJson(defaults));
            } catch (IOException exception) {
                SocialCoreGt.LOGGER.error("Could not write default config to {}", CONFIG_PATH, exception);
            }
            return defaults;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            SocialCoreConfig config = GSON.fromJson(reader, SocialCoreConfig.class);
            if (config == null) {
                throw new JsonParseException("Config file is empty");
            }
            return config;
        } catch (IOException | JsonParseException exception) {
            SocialCoreGt.LOGGER.error("Could not load config from {}; using defaults", CONFIG_PATH, exception);
            return new SocialCoreConfig();
        }
    }

    public boolean isClientScreenIntegrationEnabled() {
        return this.clientScreenIntegrationEnabled;
    }
}
