package dev.ftb.extendedexchange.client.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import org.lwjgl.opengl.GL11;

public class HighlightButton extends EXButton {
    public HighlightButton(int x, int y) {
        super(x, y, 14, 14, b -> { });
    }

    public HighlightButton(int x, int y, int w, int h) {
        super(x, y, w, h, b -> { });
    }

    public HighlightButton(int x, int y, int w, int h, OnPress onPress) {
        super(x, y, w, h, onPress);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (isHoveredOrFocused()) {
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            fill(poseStack, x, y, x + width, y + height, 0x80FFFFFF);
            RenderSystem.disableBlend();
        }
    }
}
