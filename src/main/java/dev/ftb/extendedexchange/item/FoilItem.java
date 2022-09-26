package dev.ftb.extendedexchange.item;

import net.minecraft.world.item.ItemStack;

public class FoilItem extends BasicItem {
    @Override
    public boolean isFoil(ItemStack stac) {
        return true;
    }
}
