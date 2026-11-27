package dev.socialcore;

import dev.socialcore.config.SocialCoreConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class SocialCoreGt implements ModInitializer {
    public static final String MOD_ID = "socialcore-gt";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static SocialCoreConfig config;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing SocialCore-GT common components");
        config = SocialCoreConfig.load();
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                LOGGER.info("SocialCore-GT server components are ready"));
    }

    public static SocialCoreConfig config() {
        return Objects.requireNonNull(config, "SocialCore-GT common initializer has not run");
    }
}
