package dev.socialcore.client;

import dev.socialcore.SocialCoreGt;
import dev.socialcore.client.gui.SocialCoreHubScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.network.chat.Component;

public final class SocialCoreGtClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (!SocialCoreGt.config().isClientScreenIntegrationEnabled()) {
            SocialCoreGt.LOGGER.info("SocialCore-GT client screen integration is disabled by config");
            return;
        }

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof SocialInteractionsScreen)) {
                return;
            }

            int buttonWidth = Math.min(100, Math.max(1, scaledWidth - 8));
            int buttonX = scaledWidth - buttonWidth - 4;
            Button socialCoreButton = Button.builder(
                            Component.translatable("gui.socialcore-gt.open"),
                            button -> client.setScreenAndShow(new SocialCoreHubScreen(screen)))
                    .bounds(buttonX, 8, buttonWidth, 20)
                    .build();
            Screens.getWidgets(screen).add(socialCoreButton);
        });

        SocialCoreGt.LOGGER.info("SocialCore-GT client integration initialized");
    }
}
