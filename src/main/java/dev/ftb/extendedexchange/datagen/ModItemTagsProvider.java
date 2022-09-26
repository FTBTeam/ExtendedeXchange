package dev.ftb.extendedexchange.datagen;

import dev.ftb.extendedexchange.item.ModItems;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, String modId, ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        ModItems.MAGNUM_STAR.values().forEach(ro -> tag(PETags.Items.CURIOS_KLEIN_STAR).add(ro.get()));
        ModItems.COLOSSAL_STAR.values().forEach(ro -> tag(PETags.Items.CURIOS_KLEIN_STAR).add(ro.get()));
    }
}
