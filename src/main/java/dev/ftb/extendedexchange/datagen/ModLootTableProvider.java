package dev.ftb.extendedexchange.datagen;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import dev.ftb.extendedexchange.block.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return Lists.newArrayList(Pair.of(BlockLootTables::new, LootContextParamSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext tracker) {
        map.forEach((k, v) -> LootTables.validate(tracker, k, v));
    }

    public static class BlockLootTables extends BlockLoot {
        private final Set<Block> blocks = new HashSet<>();

        @Override
        protected void addTables() {
            dropSelf(ModBlocks.ENERGY_LINK.get());
            dropSelf(ModBlocks.PERSONAL_LINK.get());
            dropSelf(ModBlocks.REFINED_LINK.get());
            dropSelf(ModBlocks.COMPRESSED_REFINED_LINK.get());
            ModBlocks.COLLECTOR.forEach((k, v) -> dropSelf(v.get()));
            ModBlocks.RELAY.forEach((k, v) -> dropSelf(v.get()));
            ModBlocks.POWER_FLOWER.forEach((k, v) -> dropSelf(v.get()));
            dropSelf(ModBlocks.STONE_TABLE.get());
            dropSelf(ModBlocks.ALCHEMY_TABLE.get());
        }

        @Override
        protected void add(Block blockIn, LootTable.Builder table) {
            super.add(blockIn, table);
            this.blocks.add(blockIn);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return this.blocks;
        }
    }
}
