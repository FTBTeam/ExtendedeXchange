package dev.ftb.extendedexchange.integration.jei;

import com.google.common.collect.ImmutableList;
import dev.ftb.extendedexchange.block.entity.AbstractEMCBlockEntity;
import dev.ftb.extendedexchange.client.ClientUtils;
import dev.ftb.extendedexchange.client.gui.AbstractEXScreen;
import dev.ftb.extendedexchange.inventory.FilterSlot;
import dev.ftb.extendedexchange.menu.AbstractEXMenu;
import dev.ftb.extendedexchange.network.NetworkHandler;
import dev.ftb.extendedexchange.network.PacketJEIGhost;
import dev.ftb.extendedexchange.util.EXUtils;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class EMCLinkJEI<S extends AbstractEXScreen<M,T>, M extends AbstractEXMenu<T>, T extends AbstractEMCBlockEntity> implements IGhostIngredientHandler<S> {
    @Override
    public <I> List<Target<I>> getTargets(S gui, I ingredient, boolean doStart) {
        ImmutableList.Builder<Target<I>> builder = ImmutableList.builder();
        if (ingredient instanceof ItemStack stack
                && ProjectEAPI.getEMCProxy().hasValue(stack)
                && EXUtils.playerHasKnowledge(ClientUtils.getClientPlayer(), stack))
        {
            NonNullList<Slot> slots = gui.getMenu().slots;
            // indexed for loop needed to get raw container slot index
            for (int i = 0; i < slots.size(); i++) {
                Slot slot = slots.get(i);
                if (slot instanceof FilterSlot filterSlot) {
                    builder.add(new ItemStackTarget<>(filterSlot, i, gui));
                }
            }
        }
        return builder.build();
    }

    @Override
    public void onComplete() {
    }

    private record ItemStackTarget<I>(FilterSlot filterSlot, int rawSlot, AbstractEXScreen<?,?> gui) implements Target<I> {
        @Override
        public Rect2i getArea() {
            return new Rect2i(gui.getGuiLeft() + filterSlot.x, gui.getGuiTop() + filterSlot.y, 16, 16);
        }

        @Override
        public void accept(I ingredient) {
            if (ingredient instanceof ItemStack stack) {
                filterSlot.set(stack);
                NetworkHandler.sendToServer(new PacketJEIGhost(rawSlot, stack));
            }
        }
    }
}
