package dev.socialcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SocialCoreGt implements ModInitializer {
    public static final String MOD_ID = "socialcore-gt";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing SocialCore-GT common components");
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                LOGGER.info("SocialCore-GT server components are ready"));
    }
}
