package dev.ftb.extendedexchange.client.gui;

import dev.ftb.extendedexchange.block.entity.CompressedRefinedLinkBlockEntity;
import dev.ftb.extendedexchange.menu.CompressedRefinedLinkMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CompressedRefinedLinkScreen extends AbstractLinkScreen<CompressedRefinedLinkMenu, CompressedRefinedLinkBlockEntity> {
    public CompressedRefinedLinkScreen(CompressedRefinedLinkMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        imageHeight = 244;
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return TEXTURE_MK3;
    }

    @Override
    protected int getEMCLabelYPos() {
        return 151;
    }
}
