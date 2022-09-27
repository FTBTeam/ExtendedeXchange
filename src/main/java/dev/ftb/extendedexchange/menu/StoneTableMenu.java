package dev.ftb.extendedexchange.menu;

import dev.ftb.extendedexchange.EXTags;
import dev.ftb.extendedexchange.config.ConfigHelper;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class StoneTableMenu extends AbstractTableMenu {
    public StoneTableMenu(int windowId, Inventory invPlayer, BlockPos pos) {
        super(ModMenuTypes.STONE_TABLE.get(), windowId, invPlayer, pos);

        addPlayerSlots(invPlayer, 8, 135);
    }

    public StoneTableMenu(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
        this(windowId, playerInv, buf.readBlockPos());
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof IItemEmcHolder
                || !ConfigHelper.server().general.enableStoneTableWhitelist.get()
                || stack.is(EXTags.Items.STONE_TABLE_WHITELIST);
    }
}
