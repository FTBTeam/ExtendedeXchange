package dev.ftb.extendedexchange.config;

import dev.ftb.extendedexchange.EXTags;
import net.minecraft.world.item.ItemStack;

import java.math.BigInteger;

public class ConfigHelper {
    static BigInteger emcLinkMaxOutput;

    public static ClientConfig client() {
        return ConfigHolder.client;
    }

    public static ServerConfig server() {
        return ConfigHolder.server;
    }

    public static void setSearchType(SearchType searchType) {
        client().general.searchType.set(searchType);
    }

    public static boolean isStoneTableWhitelisted(ItemStack stack) {
        return !server().general.enableStoneTableWhitelist.get() || stack.is(EXTags.Items.STONE_TABLE_WHITELIST);
    }

    public static BigInteger getEMCLinkMaxOutput() {
        return emcLinkMaxOutput;
    }

    public static int getEMCLinkMaxStackSize() { return server().general.emcLinkMaxStackSize.get(); }
}
