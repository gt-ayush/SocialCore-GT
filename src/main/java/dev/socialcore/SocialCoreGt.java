package dev.socialcore;

import dev.socialcore.config.SocialCoreConfig;
import dev.socialcore.storage.PersistentSocialData;
import dev.socialcore.storage.SocialDataStore;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public final class SocialCoreGt implements ModInitializer {
    public static final String MOD_ID = "socialcore-gt";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static SocialCoreConfig config;
    private static volatile PersistentSocialData persistentData;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing SocialCore-GT common components");
        config = SocialCoreConfig.load();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            var dataDirectory = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
            try {
                persistentData = new SocialDataStore(dataDirectory).load();
            } catch (IOException exception) {
                throw new UncheckedIOException("Could not load SocialCore-GT persistent data", exception);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> persistentData = null);
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                LOGGER.info("SocialCore-GT server components are ready"));
    }

    public static SocialCoreConfig config() {
        return Objects.requireNonNull(config, "SocialCore-GT common initializer has not run");
    }

    public static PersistentSocialData persistentData() {
        return Objects.requireNonNull(persistentData, "SocialCore-GT server data has not been loaded");
    }
}
