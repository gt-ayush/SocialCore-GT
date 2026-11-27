package dev.socialcore.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public final class SocialCorePlayerScreen extends Screen {
    private final Screen parent;
    private final UUID playerId;
    private final String playerName;

    public SocialCorePlayerScreen(Screen parent, UUID playerId, String playerName) {
        super(Component.translatable("gui.socialcore-gt.player.title", playerName));
        this.parent = parent;
        this.playerId = playerId;
        this.playerName = playerName;
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

    public UUID playerId() {
        return this.playerId;
    }

    public String playerName() {
        return this.playerName;
    }
}
