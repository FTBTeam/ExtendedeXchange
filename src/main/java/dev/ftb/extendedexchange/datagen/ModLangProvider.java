package dev.ftb.extendedexchange.datagen;

import dev.ftb.extendedexchange.Matter;
import dev.ftb.extendedexchange.Star;
import dev.ftb.extendedexchange.block.ModBlocks;
import dev.ftb.extendedexchange.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

class ModLangProvider extends LanguageProvider {
    public ModLangProvider(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.extendedexchange", "ExtendedeXchange");
        addBlock(ModBlocks.ENERGY_LINK, "Energy EMC Link");
        addBlock(ModBlocks.PERSONAL_LINK, "Personal EMC Link");
        addBlock(ModBlocks.REFINED_LINK, "Refined EMC Link");
        addBlock(ModBlocks.COMPRESSED_REFINED_LINK, "Compressed Refined EMC Link");

        for (Matter matter : Matter.VALUES) {
            if (matter.hasMatterItem) {
                addItem(matter.getItem(), matter.displayName + " Matter");
            }

            addBlock(ModBlocks.COLLECTOR.get(matter), matter.displayName + " Energy Collector " + matter.getMK());
            addBlock(ModBlocks.RELAY.get(matter), matter.displayName + " Anti-Matter Relay " + matter.getMK());
            addItem(ModItems.COMPRESSED_COLLECTOR.get(matter), matter.displayName + " Compressed Energy Collector " + matter.getMK());
            addBlock(ModBlocks.POWER_FLOWER.get(matter), matter.displayName + " Power Flower " + matter.getMK());
        }

        addBlock(ModBlocks.STONE_TABLE, "Stone Transmutation Table");
        addBlock(ModBlocks.ALCHEMY_TABLE, "Alchemy Table");

        for (Star star : Star.VALUES) {
            addItem(ModItems.MAGNUM_STAR.get(star), "Magnum Star " + star.displayName);
            addItem(ModItems.COLOSSAL_STAR.get(star), "Colossal Star " + star.displayName);
        }

        addItem(ModItems.FINAL_STAR_SHARD, "Final Star Shard");
        addItem(ModItems.FINAL_STAR, "The Final Star");
        addItem(ModItems.KNOWLEDGE_SHARING_BOOK, "Knowledge Sharing Book");
        addItem(ModItems.ARCANE_TABLET, "Arcane Transmutation Tablet");

        add("block.extendedexchange.energy_link.tooltip", "You can use this block to add EMC to your Transmutation Table using Collectors.");
        add("block.extendedexchange.personal_link.tooltip", "Same as Basic Energy EMC Link, but also allows to import and export items.");
        add("block.extendedexchange.refined_link.tooltip", "Same as Personal EMC Link, but has 1 input slot and 9 output slots. Designed to be used with Refined Storage-like systems. It also learns items from input slots.");
        add("block.extendedexchange.compressed_refined_link.tooltip", "Same as Refined EMC Link, but has 54 output slots.");

        add("block.extendedexchange.collector.tooltip", "Server TPS friendly. Generates EMC only once a second.");
        add("block.extendedexchange.collector.emc_produced", "Produced EMC: %s/s");

        add("block.extendedexchange.relay.tooltip", "Server TPS friendly. Transfers EMC only once a second.");
        add("block.extendedexchange.relay.max_transfer", "Max EMC Transfer: %s/s");
        add("block.extendedexchange.relay.relay_bonus", "Relay Bonus: %s/s");

        add("block.extendedexchange.stone_table.learn", "Learn");
        add("block.extendedexchange.stone_table.unlearn", "Unlearn");

        add("item.extendedexchange.final_star.tooltip", "Place a chest next to Pedestal and drop item on top of it to clone it infinitely.");

        add("gui.stone_table.cant_use", "Can't use this item in Stone Table!");
        add("gui.arcane_tablet.rotate", "Rotate");
        add("gui.arcane_tablet.balance", "Balance / Spread");
        add("gui.arcane_tablet.clear", "Clear");

        /*
        extendedexchange=Project EX Common
        extendedexchange.general.enable_stone_table_whitelist=Enable Stone Table Whitelist
        extendedexchange.general.stone_table_whitelist=Stone Table Whitelist
        extendedexchange.general.override_emc_formatter=Override EMC Formatter
        extendedexchange.general.blacklist_power_flower_from_watch=Blacklist Power Flower from Watch
        extendedexchange.general.final_star_copy_any_item=Final Star Can Copy Any Item
        extendedexchange.general.final_star_copy_nbt=Final Star Can Copy NBT
        extendedexchange.general.emc_link_max_out=EMC Link Max Item Output

        extendedexchange.tiers.collector_output=Collector Output
        extendedexchange.tiers.relay_bonus=Relay Bonus
        extendedexchange.tiers.relay_transfer=Relay Transfer

        extendedexchange_client=Project EX Client
        extendedexchange.general.emc_screen_position=EMC Screen Position
        extendedexchange.general.search_type=Search Type
        extendedexchange.general.search_type.normal=Normal
        extendedexchange.general.search_type.autoselected=Auto-selected
        extendedexchange.general.search_type.normal_jei_sync=Normal (JEI Sync)
        extendedexchange.general.search_type.autoselected_jei_sync=Auto-selected (JEI Sync)
         */
    }
}