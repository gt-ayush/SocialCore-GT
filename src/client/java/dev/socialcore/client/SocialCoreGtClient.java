package dev.socialcore.client;

import dev.socialcore.SocialCoreGt;
import net.fabricmc.api.ClientModInitializer;

public final class SocialCoreGtClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (!SocialCoreGt.config().isClientScreenIntegrationEnabled()) {
            SocialCoreGt.LOGGER.info("SocialCore-GT client screen integration is disabled by config");
            return;
        }

        SocialCoreGt.LOGGER.info("SocialCore-GT client integration initialized");
    }
}
