package dev.ftb.extendedexchange.client.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.extendedexchange.client.gui.AbstractEXScreen;
import org.lwjgl.opengl.GL11;

public class ArrowButton extends EXButton {
    public ArrowButton(int x, int y, OnPress onPress) {
        super(x, y, 18, 18, onPress);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (isHoveredOrFocused()) {
            AbstractEXScreen.bindTexture(texture);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            blit(poseStack, x, y, textureX, textureY, width, height);
            RenderSystem.disableBlend();
        }
    }
}
