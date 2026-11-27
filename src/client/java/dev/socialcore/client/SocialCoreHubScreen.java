package dev.socialcore.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

final class SocialCoreHubScreen extends Screen {
    private final Screen parent;

    SocialCoreHubScreen(Screen parent) {
        super(Component.translatable("gui.socialcore-gt.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = Math.min(120, Math.max(1, this.width - 8));
        int buttonX = (this.width - buttonWidth) / 2;
        int buttonY = this.height / 2 + 20;

        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.socialcore-gt.back"),
                        button -> Minecraft.getInstance().setScreenAndShow(this.parent))
                .bounds(buttonX, buttonY, buttonWidth, 20)
                .build());
    }
}
