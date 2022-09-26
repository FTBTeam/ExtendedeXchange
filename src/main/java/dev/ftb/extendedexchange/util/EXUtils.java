package dev.ftb.extendedexchange.util;

import dev.ftb.extendedexchange.ExtendedExchange;
import net.minecraft.resources.ResourceLocation;

public class EXUtils {
    public static ResourceLocation rl(String path) {
        return new ResourceLocation(ExtendedExchange.MOD_ID, path);
    }
}
