package dev.ftb.extendedexchange.integration.jei;

import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.recipes.AlchemyTableRecipe;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeTypes {
    public static final RecipeType<AlchemyTableRecipe> ALCHEMY_TABLE = register("alchemy_table", AlchemyTableRecipe.class);

    private static <T extends Recipe<?>> RecipeType<T> register(String name, Class<T> recipeClass) {
        return RecipeType.create(ExtendedExchange.MOD_ID, name, recipeClass);
    }
}
