package dev.ftb.extendedexchange.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.block.entity.AbstractEMCBlockEntity;
import dev.ftb.extendedexchange.menu.AbstractEXMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractEXScreen<C extends AbstractEXMenu<T>, T extends AbstractEMCBlockEntity> extends AbstractContainerScreen<C> {
    public AbstractEXScreen(C menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    public static void bindTexture(ResourceLocation guiTexture) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, guiTexture);
        RenderSystem.enableTexture();
    }

    final void bindGuiTexture() {
        ResourceLocation guiTexture = getGuiTexture();
        if (guiTexture != null) {
            bindTexture(guiTexture);
        }
    }

    protected abstract ResourceLocation getGuiTexture();

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        bindGuiTexture();

        int xStart = (width - imageWidth) / 2;
        int yStart = (height - imageHeight) / 2;
        blit(poseStack, xStart, yStart, 0, 0, imageWidth, imageHeight);
    }
}
