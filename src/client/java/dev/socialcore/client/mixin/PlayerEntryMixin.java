package dev.socialcore.client.mixin;

import dev.socialcore.SocialCoreGt;
import dev.socialcore.client.gui.SocialCorePlayerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mixin(PlayerEntry.class)
public abstract class PlayerEntryMixin {
    @Shadow
    @Final
    @Mutable
    private List<AbstractWidget> children;

    @Unique
    private Button socialcore$actionsButton;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void socialcore$addActionsButton(
            Minecraft minecraft,
            SocialInteractionsScreen parent,
            UUID playerId,
            String playerName,
            Supplier<PlayerSkin> skinGetter,
            boolean reportingEnabled,
            CallbackInfo callbackInfo
    ) {
        if (!SocialCoreGt.config().isClientScreenIntegrationEnabled()) {
            return;
        }

        this.children = new ArrayList<>(this.children);
        this.socialcore$actionsButton = Button.builder(
                        Component.translatable("gui.socialcore-gt.player.actions_short"),
                        button -> minecraft.setScreenAndShow(
                                new SocialCorePlayerScreen(parent, playerId, playerName)))
                .bounds(0, 0, 20, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.socialcore-gt.player.actions")))
                .build();
        this.children.add(this.socialcore$actionsButton);
    }

    @Inject(method = "extractContent", at = @At("TAIL"))
    private void socialcore$positionAndRenderActionsButton(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            boolean hovered,
            float delta,
            CallbackInfo callbackInfo
    ) {
        if (this.socialcore$actionsButton == null) {
            return;
        }

        PlayerEntry entry = (PlayerEntry) (Object) this;
        this.socialcore$actionsButton.setX(entry.getContentX() + entry.getContentWidth() - 52);
        this.socialcore$actionsButton.setY(entry.getContentY() + (entry.getContentHeight() - 20) / 2);
        this.socialcore$actionsButton.extractRenderState(graphics, mouseX, mouseY, delta);
    }
}
