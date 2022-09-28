package dev.ftb.extendedexchange.integration.jei;

import dev.ftb.extendedexchange.menu.ArcaneTabletMenu;
import dev.ftb.extendedexchange.network.NetworkHandler;
import dev.ftb.extendedexchange.network.PacketArcaneTabletRecipeTransfer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ArcaneTabletTransfer implements IRecipeTransferHandler<ArcaneTabletMenu, CraftingRecipe> {
    private final IRecipeTransferHandlerHelper transferHelper;

    public ArcaneTabletTransfer(IRecipeTransferHandlerHelper transferHelper) {
        this.transferHelper = transferHelper;
    }

    @Override
    public Class<ArcaneTabletMenu> getContainerClass() {
        return ArcaneTabletMenu.class;
    }

    @Override
    public Class<CraftingRecipe> getRecipeClass() {
        return CraftingRecipe.class;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(ArcaneTabletMenu container, CraftingRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            List<IRecipeSlotView> views = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
            List<Slot> slots = container.slots.stream().filter(s -> s.container instanceof CraftingContainer).toList();
            if (views.size() != slots.size()) {
                return transferHelper.createInternalError();
            }
            Int2ObjectMap<List<ItemStack>> map = new Int2ObjectOpenHashMap<>();
            for (int i = 0; i < views.size(); i++) {
                map.put(i, views.get(i).getIngredients(VanillaTypes.ITEM_STACK).collect(Collectors.toList()));
            }
            NetworkHandler.sendToServer(new PacketArcaneTabletRecipeTransfer(map, maxTransfer));
        }

        return null;

    }
}
