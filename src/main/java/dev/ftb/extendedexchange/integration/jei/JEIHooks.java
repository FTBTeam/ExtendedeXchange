package dev.ftb.extendedexchange.integration.jei;

import net.minecraftforge.fml.ModList;

public class JEIHooks {
    public static void handleJEISync(String text) {
        if (ModList.get().isLoaded("jei")) {
            JEIIntegration.setFilterText(text);
        }
    }
}
