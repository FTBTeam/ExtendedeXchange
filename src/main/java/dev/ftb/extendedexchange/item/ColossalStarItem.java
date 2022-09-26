package dev.ftb.extendedexchange.item;

import dev.ftb.extendedexchange.Star;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ColossalStarItem extends MagnumStarItem {
    public ColossalStarItem(Star t) {
        super(t);
    }

    @Override
    public long getMaximumEmc(@Nonnull ItemStack stack) {
        return tier.getMaxEMC(true);
    }
}
