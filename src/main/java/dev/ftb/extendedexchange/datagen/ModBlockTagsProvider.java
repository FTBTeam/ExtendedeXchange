package dev.ftb.extendedexchange.datagen;

import dev.ftb.extendedexchange.block.ModBlocks;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.common.data.ExistingFileHelper;

class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator generatorIn, String modId, ExistingFileHelper existingFileHelper) {
        super(generatorIn, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        ModBlocks.REGISTRY.getEntries().forEach(ro -> {
            Block block = ro.get();
            if (!(block instanceof LiquidBlock) && !(block instanceof AirBlock)) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
                tag(BlockTags.NEEDS_STONE_TOOL).add(block);
            }
        });

        ModBlocks.POWER_FLOWER.values().forEach(p -> tag(PETags.Blocks.BLACKLIST_TIME_WATCH).add(p.get()));
    }
}
