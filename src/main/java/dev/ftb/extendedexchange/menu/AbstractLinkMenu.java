package dev.ftb.extendedexchange.menu;

import dev.ftb.extendedexchange.block.entity.AbstractLinkInvBlockEntity;
import dev.ftb.extendedexchange.inventory.FilterSlot;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class AbstractLinkMenu<T extends AbstractLinkInvBlockEntity> extends AbstractEXMenu<T> {
    public AbstractLinkMenu(MenuType<?> type, int windowId, Inventory invPlayer, BlockPos tilePos) {
        super(type, windowId, invPlayer, tilePos);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();

            long value = ProjectEAPI.getEMCProxy().getValue(stack);
            if (value == 0) return ItemStack.EMPTY;

            ItemStack oldStack = stack.copy();
            player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).ifPresent(provider -> {
                ItemInfo fixed = ProjectEAPI.getEMCProxy().getPersistentInfo(ItemInfo.fromStack(stack));
                provider.addKnowledge(fixed);
                getBlockEntity().addToOutput(fixed.createStack());
                long actualEmc = (long) (stack.getCount() * value * ProjectEConfig.server.difficulty.covalenceLoss.get());
                getBlockEntity().insertEmc(actualEmc, IEmcStorage.EmcAction.EXECUTE);
            });

            slot.set(ItemStack.EMPTY);
            return oldStack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (player instanceof ServerPlayer serverPlayer && slotId >= 0 && slotId < slots.size()) {
            Slot slot = slots.get(slotId);
            if (slot instanceof FilterSlot filterSlot) {
                switch (clickType) {
                    case QUICK_MOVE -> slot.set(ItemStack.EMPTY);
                    case PICKUP -> {
                        if (!getCarried().isEmpty()) {
                            ItemStack fixed = ProjectEAPI.getEMCProxy().getPersistentInfo(ItemInfo.fromStack(getCarried())).createStack();

                            // prevent duplicate items in the filter slots
                            for (int i = 0; i < getBlockEntity().getOutputHandler().getSlots(); i++) {
                                if (getBlockEntity().getOutputHandler().getStackInSlot(i).getItem() == fixed.getItem()) {
                                    return;
                                }
                            }
                            slot.set(fixed);
                            player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).ifPresent(provider -> {
                                if (provider.addKnowledge(getCarried())) {
                                    provider.sync(serverPlayer);
                                }
                            });
                        } else if (slot.hasItem()) {
                            int amount = button == 0 ? slot.getItem().getMaxStackSize() : 1;
                            ItemStack extracted = filterSlot.remove(amount);
                            ItemHandlerHelper.giveItemToPlayer(player, extracted);
                        }
                    }
                }
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
    }
}
