package dev.ftb.extendedexchange.datagen.recipes;

import com.google.gson.JsonObject;
import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.recipes.AlchemyTableRecipe;
import net.minecraft.resources.ResourceLocation;

public class AlchemyTableRecipeBuilder extends EXRecipeBuilder<AlchemyTableRecipeBuilder> {
    private static final ResourceLocation ID = new ResourceLocation(ExtendedExchange.MOD_ID, "alchemy_table");
    private final AlchemyTableRecipe recipe;

    public AlchemyTableRecipeBuilder(AlchemyTableRecipe recipe) {
        super(ID);
        this.recipe = recipe;
    }

    @Override
    protected EXRecipeBuilder<AlchemyTableRecipeBuilder>.RecipeResult getResult(ResourceLocation id) {
        return new RecipeResult(id) {
            @Override
            public void serializeRecipeData(JsonObject json) {
                recipe.toJson(json);
            }
        };
    }
}
