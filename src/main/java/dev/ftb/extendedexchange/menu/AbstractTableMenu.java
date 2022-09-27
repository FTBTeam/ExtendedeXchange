package dev.ftb.extendedexchange.menu;

import dev.ftb.extendedexchange.block.entity.AbstractEMCBlockEntity;
import dev.ftb.extendedexchange.util.EXUtils;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public abstract class AbstractTableMenu extends AbstractEXMenu<AbstractEMCBlockEntity> implements IGuiButtonListener {
    private KnowledgeUpdater knowledgeUpdater;
    private final Player player;
    private final IKnowledgeProvider provider;

    public AbstractTableMenu(MenuType<?> type, int windowId, Inventory invPlayer, BlockPos pos) {
        super(type, windowId, invPlayer, pos);

        this.player = invPlayer.player;
        this.provider = ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(player.getUUID());
    }

    @NotNull
    @Override
    protected Class<AbstractEMCBlockEntity> blockEntityClass() {
        return AbstractEMCBlockEntity.class;
    }

    @Override
    public void handleGUIButtonPress(String tag, boolean shiftHeld, ServerPlayer player) {
        System.out.println("handle button: " + tag);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        ItemStack stack = slot.getItem();

        if (!stack.isEmpty()) {
            if (!ProjectEAPI.getEMCProxy().hasValue(stack)) {
                return ItemStack.EMPTY;
            }

            ItemStack stack1 = ProjectEAPI.getEMCProxy().getPersistentInfo(ItemInfo.fromStack(stack)).createStack();
            if (!isItemValid(stack1)) {
                return ItemStack.EMPTY;
            }

            switch (EXUtils.addKnowledge(player, provider, stack1)) {
                case NOT_ADDED:
                    return ItemStack.EMPTY;
                case ADDED:
                    if (knowledgeUpdater != null) knowledgeUpdater.onKnowledgeUpdate();
                    break;
            }

            long toAdd = (long) (ProjectEAPI.getEMCProxy().getValue(stack) * stack.getCount() * ProjectEConfig.server.difficulty.covalenceLoss.get());
            provider.setEmc(provider.getEmc().add(BigInteger.valueOf(toAdd)));
            slot.set(ItemStack.EMPTY);
            return stack1;
        }

        return ItemStack.EMPTY;
    }

    protected boolean isItemValid(ItemStack stack) {
        return true;
    }

    public IKnowledgeProvider getProvider() {
        return provider;
    }

    public void setKnowledgeUpdater(KnowledgeUpdater knowledgeUpdater) {
        this.knowledgeUpdater = knowledgeUpdater;
    }

    @FunctionalInterface
    public interface KnowledgeUpdater {
        void onKnowledgeUpdate();
    }
}
