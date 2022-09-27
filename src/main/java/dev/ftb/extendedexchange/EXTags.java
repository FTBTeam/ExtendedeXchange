package dev.ftb.extendedexchange;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class EXTags {
    public static class Items {
        public static final TagKey<Item> STONE_TABLE_WHITELIST = modTag("stone_table_whitelist");

        static TagKey<Item> tag(String modid, String name) {
            return Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(new ResourceLocation(modid, name));
        }

        static TagKey<Item> modTag(String name) {
            return tag(ExtendedExchange.MOD_ID, name);
        }

        static TagKey<Item> forgeTag(String name) {
            return tag("forge", name);
        }
    }
}
