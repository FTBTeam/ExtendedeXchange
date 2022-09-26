package dev.ftb.extendedexchange.menu;

import dev.ftb.extendedexchange.block.entity.RefinedLinkBlockEntity;
import dev.ftb.extendedexchange.inventory.FilterSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.SlotItemHandler;

public class RefinedLinkMenu extends AbstractLinkMenu<RefinedLinkBlockEntity> {
    public RefinedLinkMenu(int windowId, Inventory playerInv, BlockPos pos) {
        super(ModMenuTypes.REFINED_LINK.get(), windowId, playerInv, pos);

        addSlot(new SlotItemHandler(getBlockEntity().getInputHandler(), 0, 35, 35));

        for (int i = 0; i < 9; i++) {
            addSlot(new FilterSlot(getBlockEntity().getOutputHandler(), i, 89 + (i % 3) * 18, 17 + (i / 3) * 18));
        }

        addPlayerSlots(playerInv, 8, 84);
    }

    public RefinedLinkMenu(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
        this(windowId, playerInv, buf.readBlockPos());
    }

    @Override
    protected Class<RefinedLinkBlockEntity> blockEntityClass() {
        return RefinedLinkBlockEntity.class;
    }
}
