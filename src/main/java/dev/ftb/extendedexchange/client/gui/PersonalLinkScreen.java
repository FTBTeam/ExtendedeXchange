package dev.ftb.extendedexchange.client.gui;

import dev.ftb.extendedexchange.block.entity.PersonalLinkBlockEntity;
import dev.ftb.extendedexchange.menu.PersonalLinkMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PersonalLinkScreen extends AbstractLinkScreen<PersonalLinkMenu, PersonalLinkBlockEntity> {
    public PersonalLinkScreen(PersonalLinkMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return TEXTURE_MK1;
    }
}
