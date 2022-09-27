package dev.ftb.extendedexchange.client.gui.buttons;

import dev.ftb.extendedexchange.network.NetworkHandler;
import dev.ftb.extendedexchange.network.PacketGuiButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public abstract class EXButton extends Button {
    protected ResourceLocation texture;
    protected int textureX;
    protected int textureY;
    private String tag = "";

    public EXButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, TextComponent.EMPTY, onPress);
    }

    @Override
    public void onPress() {
        super.onPress();

        if (tag != null && !tag.isEmpty()) NetworkHandler.sendToServer(new PacketGuiButton(tag, Screen.hasShiftDown()));
    }

    public EXButton withTag(String tag) {
        this.tag = tag;
        return this;
    }

    public EXButton withTexture(ResourceLocation texture, int tx, int ty) {
        this.texture = texture;
        this.textureX = tx;
        this.textureY = ty;
        return this;
    }
}
