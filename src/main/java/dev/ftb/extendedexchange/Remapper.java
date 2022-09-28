package dev.ftb.extendedexchange;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ExtendedExchange.MOD_ID)
public class Remapper {
    // Temporary!

    @SubscribeEvent
    public static void blocks(RegistryEvent.MissingMappings<Block> event) {
        event.getMappings("projectex").forEach(mapping -> {
            Block remapped = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ExtendedExchange.MOD_ID, mapping.key.getPath()));
            mapping.remap(remapped);
        });
    }
    @SubscribeEvent
    public static void items(RegistryEvent.MissingMappings<Item> event) {
        event.getMappings("projectex").forEach(mapping -> {
            Item remapped = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ExtendedExchange.MOD_ID, mapping.key.getPath()));
            mapping.remap(remapped);
        });
    }
    @SubscribeEvent
    public static void menus(RegistryEvent.MissingMappings<MenuType<?>> event) {
        event.getMappings("projectex").forEach(mapping -> {
            MenuType<?> remapped = ForgeRegistries.CONTAINERS.getValue(new ResourceLocation(ExtendedExchange.MOD_ID, mapping.key.getPath()));
            mapping.remap(remapped);
        });
    }
    @SubscribeEvent
    public static void blockEntities(RegistryEvent.MissingMappings<BlockEntityType<?>> event) {
        event.getMappings("projectex").forEach(mapping -> {
            BlockEntityType<?> remapped = ForgeRegistries.BLOCK_ENTITIES.getValue(new ResourceLocation(ExtendedExchange.MOD_ID, mapping.key.getPath()));
            mapping.remap(remapped);
        });
    }
    @SubscribeEvent
    public static void recipeSerializers(RegistryEvent.MissingMappings<RecipeSerializer<?>> event) {
        event.getMappings("projectex").forEach(mapping -> {
            RecipeSerializer<?> remapped = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ExtendedExchange.MOD_ID, mapping.key.getPath()));
            mapping.remap(remapped);
        });
    }
}
