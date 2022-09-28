package dev.ftb.extendedexchange.menu;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import dev.ftb.extendedexchange.util.EXUtils;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.math.BigInteger;
import java.util.*;

public class ArcaneTabletMenu extends AbstractTableMenu {
    private static final int[] ROTATION_SLOTS = {0, 1, 2, 5, 8, 7, 6, 3};

    private final CraftingContainer craftMatrix;
    private final ResultContainer craftResult;
    public boolean skipRefill = false;

    public ArcaneTabletMenu(int windowId, Inventory invPlayer) {
        super(ModMenuTypes.ARCANE_TABLET.get(), windowId, invPlayer, null);

        craftMatrix = new ArcaneTabletCraftingContainer(this, 3, 3, (IItemHandlerModifiable) getProvider().getInputAndLocks());
        craftResult = new ResultContainer();

        addSlot(new ArcaneTabletResultSlot(invPlayer.player, craftMatrix, craftResult, 0, -23, 75));
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new Slot(craftMatrix, x + y * 3, -59 + x * 18, 17 + y * 18));
            }
        }
        addPlayerSlots(invPlayer, 8, 135);

        slotChangedCraftingGrid(this, invPlayer.player.level, invPlayer.player, craftMatrix, craftResult);
    }

    public ArcaneTabletMenu(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
        this(windowId, playerInv);
    }

    @Override
    public void handleGUIButtonPress(String tag, boolean shiftHeld, ServerPlayer player) {
        switch (tag) {
            case "clear" -> {
                clearCraftingMatrix();
                return;
            }
            case "rotate" -> {
                rotateCraftingMatrix(!shiftHeld);
                return;
            }
            case "balance" -> {
                if (shiftHeld) {
                    spreadCraftingMatrix();
                } else {
                    balanceCraftingMatrix();
                }
                return;
            }
        }
        super.handleGUIButtonPress(tag, shiftHeld, player);
    }

    static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer container, ResultContainer result) {
        // copied from CraftingMenu.slotChangedCraftingGrid, which has protected access
        CraftingRecipe craftingRecipe;
        if (level.isClientSide) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ItemStack itemStack = ItemStack.EMPTY;
        Optional<CraftingRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, level);
        if (optional.isPresent() && result.setRecipeUsed(level, serverPlayer, craftingRecipe = optional.get())) {
            itemStack = craftingRecipe.assemble(container);
        }
        result.setItem(0, itemStack);
        menu.setRemoteSlot(0, itemStack);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, itemStack));
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);

        slotChangedCraftingGrid(this, player.getLevel(), player, craftMatrix, craftResult);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index == 0) {
            Slot slot = slots.get(index);

            if (slot.hasItem()) {
                ItemStack stack = slot.getItem();
                ItemStack oldStack = stack.copy();

                if (!moveItemStackTo(stack, playerSlotsStart, playerSlotsStart + 36, true)) {
                    return ItemStack.EMPTY;
                }

                if (stack.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }

                if (stack.getCount() == oldStack.getCount()) {
                    return ItemStack.EMPTY;
                }

                player.drop(stack, false);
                return oldStack;
            }

            return ItemStack.EMPTY;
        }
        return super.quickMoveStack(player, index);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (clickType == ClickType.QUICK_MOVE) skipRefill = true;
        super.clicked(slotId, button, clickType, player);
        if (clickType == ClickType.QUICK_MOVE) skipRefill = false;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != craftResult && super.canTakeItemForPickAll(stack, slot);
    }

    public void onRecipeTransfer(ItemStack[][] recipe, boolean transferAll) {
        clearCraftingMatrix();

        int max = Math.min(recipe.length, craftMatrix.getContainerSize());
        transferItems(recipe, max);

        if (transferAll) {
            for (int i = 0; i < 63; i++) {
                transferItems(recipe, max);
            }
        }

        slotChangedCraftingGrid(this, player.level, player, craftMatrix, craftResult);
        broadcastChanges();
    }

    private void transferItems(ItemStack[][] recipe, int max) {
        for (int i = 0; i < max; i++) {
            if (recipe[i] != null && recipe[i].length > 0) {
                if (transferFromInventory(i, recipe[i])) {
                    //recipe[i] = null;
                }
            }
        }

        for (int i = 0; i < max; i++) {
            if (recipe[i] != null && recipe[i].length > 0) {
                if (transferFromTablet(i, recipe[i])) {
                    //recipe[i] = null;
                }
            }
        }
    }

    private boolean transferFromTablet(int slot, ItemStack[] possibilities) {
        if (possibilities.length > 1) {
            Arrays.sort(possibilities, Comparator.comparingLong(o -> ProjectEAPI.getEMCProxy().getValue(o)));
        }

        for (ItemStack stack : possibilities) {
            ItemStack stack1 = ProjectEAPI.getEMCProxy().getPersistentInfo(ItemInfo.fromStack(stack)).createStack();

            if (provider.hasKnowledge(stack1)) {
                long value = ProjectEAPI.getEMCProxy().getValue(stack1);

                BigInteger bigValue = BigInteger.valueOf(value);
                if (value > 0L && provider.getEmc().compareTo(bigValue) > 0) {
                    ItemStack slotItem = craftMatrix.getItem(slot);

                    if (slotItem.isEmpty()) {
                        craftMatrix.setItem(slot, stack1);
                    } else if (slotItem.getCount() < slotItem.getMaxStackSize()
                            && slotItem.getItem() == stack1.getItem()
                            && Objects.equals(slotItem.getItem().getShareTag(slotItem), stack1.getItem().getShareTag(stack1))) {
                        slotItem.grow(1);
                    } else {
                        continue;
                    }
                    provider.setEmc(provider.getEmc().subtract(bigValue));
                    if (player instanceof ServerPlayer sp) provider.syncEmc(sp);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean transferFromInventory(int slot, ItemStack[] possibilities) {
        for (ItemStack possibility : possibilities) {
            ItemStack fixedp = ProjectEAPI.getEMCProxy().getPersistentInfo(ItemInfo.fromStack(possibility)).createStack();

            for (int j = 0; j < player.getInventory().getContainerSize(); ++j) {
                ItemStack stack = player.getInventory().getItem(j);
                ItemStack stack1 = ProjectEAPI.getEMCProxy().getPersistentInfo(ItemInfo.fromStack(stack)).createStack();

                if (ItemStack.isSameItemSameTags(fixedp, stack1)) {
                    ItemStack slotItem = craftMatrix.getItem(slot);

                    if (slotItem.isEmpty()) {
                        craftMatrix.setItem(slot, ItemHandlerHelper.copyStackWithSize(stack, 1));
                    } else if (slotItem.getCount() < slotItem.getMaxStackSize()
                            && slotItem.getItem() == stack.getItem()
                            && Objects.equals(slotItem.getItem().getShareTag(slotItem), stack.getItem().getShareTag(stack))) {
                        slotItem.grow(1);
                    } else {
                        continue;
                    }

                    stack.shrink(1);

                    if (stack.isEmpty()) {
                        player.getInventory().setItem(j, ItemStack.EMPTY);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public void clearCraftingMatrix() {
        for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
            ItemStack stack = craftMatrix.removeItemNoUpdate(i);
            if (!stack.isEmpty()) {
                if (ProjectEConfig.server.difficulty.covalenceLoss.get() >= 1D && ProjectEAPI.getEMCProxy().hasValue(stack)) {
                    EXUtils.addKnowledge(player, provider, ProjectEAPI.getEMCProxy().getPersistentInfo(ItemInfo.fromStack(stack)).createStack());
                }
                player.getInventory().placeItemBackInInventory(stack, true);
            }
        }
        slotChangedCraftingGrid(this, player.level, player, craftMatrix, craftResult);
    }

    public void rotateCraftingMatrix(boolean clockwise) {
        ItemStack[] stacks = new ItemStack[ROTATION_SLOTS.length];

        if (clockwise) {
            for (int i = 0; i < ROTATION_SLOTS.length; i++) {
                int j = i - 1;
                if (j < 0) {
                    j = ROTATION_SLOTS.length - 1;
                }
                stacks[i] = craftMatrix.getItem(ROTATION_SLOTS[j % ROTATION_SLOTS.length]);
            }
        } else {
            for (int i = 0; i < ROTATION_SLOTS.length; i++) {
                stacks[i] = craftMatrix.getItem(ROTATION_SLOTS[(i + 1) % ROTATION_SLOTS.length]);
            }
        }

        for (int i = 0; i < ROTATION_SLOTS.length; i++) {
            craftMatrix.setItem(ROTATION_SLOTS[i], stacks[i]);
        }

        slotChangedCraftingGrid(this, player.level, player, craftMatrix, craftResult);
        broadcastChanges();
    }

    public void balanceCraftingMatrix() {
        ArrayListMultimap<CompoundTag, ItemStack> map = ArrayListMultimap.create();
        Multiset<CompoundTag> itemCount = HashMultiset.create();

        for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
            ItemStack stack = craftMatrix.getItem(i);
            if (!stack.isEmpty() && stack.getMaxStackSize() > 1) {
                CompoundTag key = stack.serializeNBT();
                key.remove("Count");
                map.put(key, stack);
                itemCount.add(key, stack.getCount());
            }
        }

        for (CompoundTag key : map.keySet()) {
            List<ItemStack> list = map.get(key);
            int totalCount = itemCount.count(key);
            int countPerStack = totalCount / list.size();
            int restCount = totalCount % list.size();

            for (ItemStack stack : list) {
                stack.setCount(countPerStack);
            }

            int idx = 0;

            while (restCount > 0) {
                ItemStack stack = list.get(idx);
                if (stack.getCount() < stack.getMaxStackSize()) {
                    stack.grow(1);
                    restCount--;
                }

                if (++idx >= list.size()) {
                    idx = 0;
                }
            }
        }

        slotChangedCraftingGrid(this, player.level, player, craftMatrix, craftResult);
        broadcastChanges();
    }

    public void spreadCraftingMatrix() {
        while (true) {
            ItemStack biggestStack = null;
            int biggestSize = 1;

            for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
                ItemStack stack = craftMatrix.getItem(i);
                if (!stack.isEmpty() && stack.getCount() > biggestSize) {
                    biggestStack = stack;
                    biggestSize = stack.getCount();
                }
            }

            if (biggestStack == null) {
                return;
            }

            boolean emptyBiggestSlot = false;

            for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
                ItemStack stack = craftMatrix.getItem(i);

                if (stack.isEmpty()) {
                    if (biggestStack.getCount() > 1) {
                        craftMatrix.setItem(i, biggestStack.split(1));
                    } else {
                        emptyBiggestSlot = true;
                    }
                }
            }

            if (!emptyBiggestSlot) {
                break;
            }
        }

        balanceCraftingMatrix();
    }

    public class ArcaneTabletResultSlot extends ResultSlot {
        public ArcaneTabletResultSlot(Player player, CraftingContainer matrix, Container result, int slot, int x, int y) {
            super(player, matrix, result, slot, x, y);
        }

//        @Override
//        protected void onCrafting(ItemStack stack) {
//            super.onCrafting(stack);
//
//            if (ProjectEAPI.getEMCProxy().hasValue(stack) && ProjectEXUtils.addKnowledge(player, playerData, ProjectEXUtils.fixOutput(stack)) == 2 && knowledgeUpdate != null) {
//                knowledgeUpdate.updateKnowledge();
//            }
//        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            if (skipRefill) {
                super.onTake(player, stack);
                return;
            }

            ItemStack[] prevItems = new ItemStack[craftMatrix.getContainerSize()];
            for (int i = 0; i < prevItems.length; i++) {
                prevItems[i] = craftMatrix.getItem(i);
                if (!prevItems[i].isEmpty()) {
                    prevItems[i] = ItemHandlerHelper.copyStackWithSize(prevItems[i], 1);
                }
            }

            super.onTake(player, stack);

            for (int i = 0; i < prevItems.length; i++) {
                if (!prevItems[i].isEmpty() && craftMatrix.getItem(i).isEmpty()) {
                    transferFromTablet(i, new ItemStack[]{prevItems[i]});
                }
            }
        }
    }

    public class ArcaneTabletCraftingContainer extends CraftingContainer {
        private final IItemHandlerModifiable items;

        public ArcaneTabletCraftingContainer(ArcaneTabletMenu tablet, int width, int height, IItemHandlerModifiable items) {
            super(tablet, width, height);

            this.items = items;
        }

        @Override
        public boolean isEmpty() {
            for (int i = 0; i < items.getSlots(); i++) {
                if (!items.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int index) {
            return index < 0 || index >= getContainerSize() ? ItemStack.EMPTY : items.getStackInSlot(index);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (!ItemStack.isSameItemSameTags(stack, getItem(slot))) {
                items.setStackInSlot(slot, stack);
                slotsChanged(this);
            }
        }

        @Override
        public ItemStack removeItemNoUpdate(int index) {
            if (index < 0 || index >= getContainerSize()) {
                return ItemStack.EMPTY;
            }
            ItemStack stack0 = items.getStackInSlot(index);
            items.setStackInSlot(index, ItemStack.EMPTY);
            return stack0;
        }

        @Override
        public ItemStack removeItem(int index, int count) {
            if (index < 0 || index >= getContainerSize() || count <= 0 || items.getStackInSlot(index).isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack stack = items.getStackInSlot(index).split(count);

            if (!stack.isEmpty()) {
                slotsChanged(this);
            }

            return stack;
        }

        @Override
        public void clearContent() {
            for (int i = 0; i < items.getSlots(); i++) {
                items.setStackInSlot(i, ItemStack.EMPTY);
            }
        }

        @Override
        public void fillStackedContents(StackedContents helper) {
            for (int i = 0; i < items.getSlots(); i++) {
                helper.accountStack(items.getStackInSlot(i));
            }
        }

        @Override
        public void setChanged() {
            slotsChanged(this);
        }
    }
}
